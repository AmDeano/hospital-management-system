package com.hospital.auth.service;

import com.hospital.auth.config.RabbitConfig;
import com.hospital.auth.dto.*;
import com.hospital.auth.entity.*;
import com.hospital.auth.repo.UserRepository;
import com.hospital.common.events.EmployeeCreatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int ACCESS_TOKEN_MINUTES = 15;
    private static final int REFRESH_TOKEN_DAYS = 7;
    private static final String TOKEN_ISSUER = "auth-service";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final DashboardService dashboardService;
    private final RabbitTemplate rabbitTemplate;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtEncoder jwtEncoder,
                       JwtDecoder jwtDecoder,
                       DashboardService dashboardService,
                       RabbitTemplate rabbitTemplate) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository is required");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "PasswordEncoder is required");
        this.jwtEncoder = Objects.requireNonNull(jwtEncoder, "JwtEncoder is required");
        this.jwtDecoder = Objects.requireNonNull(jwtDecoder, "JwtDecoder is required");
        this.dashboardService = Objects.requireNonNull(dashboardService, "DashboardService is required");
        this.rabbitTemplate = Objects.requireNonNull(rabbitTemplate, "RabbitTemplate is required");
    }

    // ==================== REGISTRATION ====================

    public void registerPatient(PatientRegisterRequest request) {
        validateNotNull(request, "Patient registration request");
        validateUniqueCredentials(request.username(), request.email());

        UserAccount account = buildUserAccount(
                request.username(),
                request.email(),
                request.password(),
                null,
                null,
                Set.of(Role.PATIENT),
                request.externalId()
        );

        userRepository.save(account);
        log.info("Patient registered successfully: {}", request.username());
    }

    public UserAccount registerEmployee(RegisterRequest request) {
        validateNotNull(request, "Employee registration request");
        validateEmployeeRequest(request);
        validateUniqueCredentials(request.matricule(), request.email());

        Set<Role> employeeRoles = filterEmployeeRoles(request.roles());
        
        UserAccount account = buildUserAccount(
                request.matricule(),
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                employeeRoles,
                defaultIfNull(request.externalId(), request.matricule())
        );

        UserAccount savedAccount = userRepository.save(account);
        publishEmployeeCreatedEvent(savedAccount, request);
        
        log.info("Employee registered successfully: {} with roles {}", 
                savedAccount.getMatricule(), employeeRoles);
        
        return savedAccount;
    }

    public UserAccount registerAdmin(RegisterRequest request) {
        validateNotNull(request, "Admin registration request");
        validateUniqueCredentials(request.matricule(), request.email());

        UserAccount account = buildUserAccount(
                request.matricule(),
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                Set.of(Role.ADMIN),
                defaultIfNull(request.externalId(), request.matricule())
        );

        UserAccount savedAccount = userRepository.save(account);
        publishEmployeeCreatedEvent(savedAccount, request);
        
        log.info("Admin registered successfully: {}", savedAccount.getMatricule());
        return savedAccount;
    }

    // ==================== AUTHENTICATION ====================

    public AuthResponse loginEmployee(LoginRequest request) {
        validateNotNull(request, "Login request");
        
        UserAccount user = findUserByMatricule(request.matricule());
        validateEmployeeCredentials(user, request.password());
        
        log.info("Employee logged in: {}", user.getMatricule());
        return buildAuthResponse(user);
    }

    public AuthResponse loginPatient(LoginRequest request) {
        validateNotNull(request, "Login request");
        
        UserAccount user = findUserByMatricule(request.matricule());
        validatePatientCredentials(user, request.password());
        
        log.info("Patient logged in: {}", user.getMatricule());
        return buildAuthResponse(user);
    }

    public AuthResponse refreshAccessToken(String refreshToken) {
        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            validateRefreshTokenType(jwt);

            UserAccount user = findUserByMatricule(jwt.getSubject());
            
            if (!user.isEnabled()) {
                throw new IllegalStateException("User account is disabled");
            }

            log.info("Token refreshed for user: {}", user.getMatricule());
            return buildAuthResponse(user);
            
        } catch (JwtException e) {
            log.warn("Invalid refresh token attempt");
            throw new IllegalArgumentException("Invalid or expired refresh token", e);
        }
    }

    // ==================== ACCOUNT MANAGEMENT ====================

    public void changePassword(String matricule, String oldPassword, String newPassword) {
        validateNotBlank(matricule, "Matricule");
        validateNotBlank(oldPassword, "Old password");
        validateNotBlank(newPassword, "New password");
        
        UserAccount user = findUserByMatricule(matricule);

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Password changed for user: {}", matricule);
    }

    public void toggleAccountStatus(String matricule, boolean enabled) {
        validateNotBlank(matricule, "Matricule");
        
        UserAccount user = findUserByMatricule(matricule);
        user.setEnabled(enabled);
        userRepository.save(user);
        
        log.info("User {} account status set to: {}", matricule, enabled ? "enabled" : "disabled");
    }

    // ==================== PRIVATE HELPERS - VALIDATION ====================

    private void validateNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    private void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
    }

    private void validateUniqueCredentials(String matricule, String email) {
        if (matricule != null && userRepository.existsByMatricule(matricule)) {
            throw new IllegalArgumentException("Matricule is already in use");
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }
    }

    private void validateEmployeeRequest(RegisterRequest request) {
        if (request.roles() == null || request.roles().isEmpty()) {
            throw new IllegalArgumentException("At least one role is required for employees");
        }
        
        if (request.roles().contains(Role.PATIENT)) {
            throw new IllegalArgumentException("PATIENT role cannot be assigned to employees");
        }
        
        if (hasAdminWithOtherRoles(request.roles())) {
            throw new IllegalArgumentException("ADMIN role cannot be combined with other roles");
        }
    }

    private boolean hasAdminWithOtherRoles(Set<Role> roles) {
        return roles.contains(Role.ADMIN) && roles.size() > 1;
    }

    private void validateEmployeeCredentials(UserAccount user, String password) {
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Account is disabled");
        }
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        if (!hasEmployeeRole(user)) {
            throw new IllegalArgumentException("Account does not have employee privileges");
        }
    }

    private void validatePatientCredentials(UserAccount user, String password) {
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Account is disabled");
        }
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        if (!hasPatientRole(user)) {
            throw new IllegalArgumentException("Account does not have patient privileges");
        }
    }

    private void validateRefreshTokenType(Jwt jwt) {
        String tokenType = jwt.getClaimAsString("type");
        if (!REFRESH_TOKEN_TYPE.equals(tokenType)) {
            throw new IllegalArgumentException("Invalid token type");
        }
    }

    // ==================== PRIVATE HELPERS - BUILDERS ====================

    private UserAccount buildUserAccount(String matricule, String email, String password,
                                         String firstName, String lastName,
                                         Set<Role> roles, String externalId) {
        validateNotBlank(matricule, "Matricule");
        validateNotBlank(password, "Password");

        UserAccount account = new UserAccount();
        account.setMatricule(matricule);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(password));
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setRoles(roles != null ? roles : Collections.emptySet());
        account.setExternalId(externalId);
        account.setEnabled(true);
        
        return account;
    }

    private AuthResponse buildAuthResponse(UserAccount user) {
        Instant now = Instant.now();
        Set<String> roleNames = extractRoleNames(user);

        String accessToken = generateAccessToken(user, roleNames, now);
        String refreshToken = generateRefreshToken(user, now);
        String dashboardRoute = determineDashboardRoute(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getMatricule(),
                user.getEmail(),
                user.getExternalId(),
                user.getRoles(),
                dashboardRoute
        );
    }

    private String generateAccessToken(UserAccount user, Set<String> roleNames, Instant now) {
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuer(TOKEN_ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_MINUTES, ChronoUnit.MINUTES))
                .subject(defaultIfNull(user.getMatricule(), "unknown"))
                .claim("uid", user.getId());

        addOptionalClaim(builder, "email", user.getEmail());
        addOptionalClaim(builder, "ext", user.getExternalId());
        
        if (roleNames != null && !roleNames.isEmpty()) {
            builder.claim("roles", roleNames);
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(builder.build())).getTokenValue();
    }

    private String generateRefreshToken(UserAccount user, Instant now) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(TOKEN_ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS))
                .subject(defaultIfNull(user.getMatricule(), "unknown"))
                .claim("type", REFRESH_TOKEN_TYPE)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private void addOptionalClaim(JwtClaimsSet.Builder builder, String key, String value) {
        if (value != null && !value.isBlank()) {
            builder.claim(key, value);
        }
    }

    // ==================== PRIVATE HELPERS - QUERIES ====================

    private UserAccount findUserByMatricule(String matricule) {
        return userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // ==================== PRIVATE HELPERS - UTILITY ====================

    private Set<Role> filterEmployeeRoles(Set<Role> requestedRoles) {
        Set<Role> employeeRoles = requestedRoles.stream()
                .filter(role -> role != Role.PATIENT)
                .collect(Collectors.toSet());
                
        if (employeeRoles.isEmpty()) {
            throw new IllegalArgumentException("At least one non-patient role is required");
        }
        
        return employeeRoles;
    }

    private boolean hasEmployeeRole(UserAccount user) {
        return user.getRoles() != null && 
               user.getRoles().stream().anyMatch(role -> role != Role.PATIENT);
    }

    private boolean hasPatientRole(UserAccount user) {
        return user.getRoles() != null && user.getRoles().contains(Role.PATIENT);
    }

    private Set<String> extractRoleNames(UserAccount user) {
        return user.getRoles() == null 
                ? Collections.emptySet()
                : user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
    }

    private String determineDashboardRoute(UserAccount user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return dashboardService.getDashboardRoute(Role.PATIENT);
        }
        
        Role primaryRole = user.getRoles().stream()
                .min(this::compareRolePriority)
                .orElse(Role.PATIENT);
                
        return dashboardService.getDashboardRoute(primaryRole);
    }

    private int compareRolePriority(Role r1, Role r2) {
        return Integer.compare(getRolePriority(r1), getRolePriority(r2));
    }

    private int getRolePriority(Role role) {
        return switch (role) {
            case ADMIN -> 1;
            case HR -> 2;
            case DOCTOR -> 3;
            case NURSE -> 4;
            case RECEPTIONIST -> 5;
            case SUPERVISOR, OBSERVATOR -> 6;
            case PATIENT -> 7;
        };
    }

    private String defaultIfNull(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    // ==================== EVENT PUBLISHING ====================

    private void publishEmployeeCreatedEvent(UserAccount user, RegisterRequest request) {
        Set<String> roleNames = extractRoleNames(user);

        EmployeeCreatedEvent event = new EmployeeCreatedEvent(
                user.getId(),
                user.getMatricule(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                request.phone(),
                request.address(),
                null,
                roleNames,
                user.getExternalId(),
                request.specialization(),
                request.licenseNumber(),
                request.medicalDegree(),
                request.shift(),
                request.nurseLicenseNumber(),
                request.deskNumber(),
                request.departmentArea(),
                request.assignedArea()
        );

        rabbitTemplate.convertAndSend(RabbitConfig.EMPLOYEE_CREATED_QUEUE, event);
        log.debug("Published EmployeeCreatedEvent for matricule: {}", user.getMatricule());
    }
}