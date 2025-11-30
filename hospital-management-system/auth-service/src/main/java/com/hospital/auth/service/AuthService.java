package com.hospital.auth.service;

import com.hospital.auth.config.RabbitConfig;
import com.hospital.auth.dto.*;
import com.hospital.auth.entity.*;
import com.hospital.auth.repo.UserRepository;
import com.hospital.common.events.EmployeeCreatedEvent;
import com.hospital.common.events.PatientEvent;

import jakarta.validation.constraints.Email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int ACCESS_TOKEN_MINUTES = 15;
    private static final int REFRESH_TOKEN_DAYS = 7;
    private static final String TOKEN_ISSUER = "auth-service";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private final PatientEventPublisher patientEventPublisher;


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
                       RabbitTemplate rabbitTemplate,
                       PatientEventPublisher patientEventPublisher) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
        this.jwtEncoder = Objects.requireNonNull(jwtEncoder);
        this.jwtDecoder = Objects.requireNonNull(jwtDecoder);
        this.dashboardService = Objects.requireNonNull(dashboardService);
        this.rabbitTemplate = Objects.requireNonNull(rabbitTemplate);
        this.patientEventPublisher = Objects.requireNonNull(patientEventPublisher);
    }

    // ==================== PATIENT REGISTRATION ====================

    public void registerPatient(PatientRegisterRequest request) {
        validateNotNull(request, "Patient registration request");
        // Use CIN or email as unique identifier
        validateUniqueEmail(request.email());
        if (request.cin() != null && !request.cin().isBlank()) {
            validateUniquecin(request.cin());
        }

        UserAccount account = buildPatientAccount(request);
        userRepository.save(account);

        log.info("✅ Patient registered successfully: {}", account.getCIN());
        publishPatientCreatedEvent(account);
    }
    private void validateUniquecin(String cin) {
        if (cin != null && !cin.isBlank()) {
            if (userRepository.existsByCIN(cin)) {
                throw new IllegalArgumentException("CIN already exists: " + cin);
            }
        }
    }

    private void validateUniqueEmail(@Email String email) {
        if (email != null && !email.isBlank() && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }


	// ==================== EMPLOYEE REGISTRATION ====================

    public UserAccount registerEmployee(RegisterRequest request) {
        validateNotNull(request, "Employee registration request");
        validateEmployeeRequest(request);
        validateUniqueCredentials(request.matricule(), request.email());

        Set<Role> employeeRoles = filterEmployeeRoles(request.roles());
        UserAccount account = buildEmployeeAccount(request, employeeRoles);
        UserAccount savedAccount = userRepository.save(account);

        publishEmployeeCreatedEvent(savedAccount, request);
        log.info("✅ Employee registered successfully: {} with roles {}", savedAccount.getMatricule(), employeeRoles);

        return savedAccount;
    }

    public UserAccount registerAdmin(RegisterRequest request) {
        validateNotNull(request, "Admin registration request");
        validateUniqueCredentials(request.matricule(), request.email());

        UserAccount account = buildEmployeeAccount(request, Set.of(Role.ADMIN));
        UserAccount savedAccount = userRepository.save(account);

        publishEmployeeCreatedEvent(savedAccount, request);
        log.info("✅ Admin registered successfully: {}", savedAccount.getMatricule());

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

    public AuthResponse loginPatient(LoginRequestEmail request) {
        validateNotNull(request, "Login request");

     // 🔍 Find patient by email
        UserAccount user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.email()));

     // Check if account has patient role
        if (user.getRoles().stream().noneMatch(r -> r == Role.PATIENT)) {
            throw new IllegalArgumentException("User is not a patient account");
        }

        // 🔐 Validate password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        log.info("Patient logged in: {}", user.getEmail());
        return buildAuthResponse(user);
    }
    
    // ==================== refreshAccessToken ====================
    
    public AuthResponse refreshAccessToken(String refreshToken) {
        try {
            // Decode the provided refresh token
            Jwt jwt = jwtDecoder.decode(refreshToken);

            // Ensure this is indeed a refresh token
            String tokenType = jwt.getClaimAsString("type");
            if (tokenType == null || !tokenType.equals("refresh")) {
                throw new IllegalArgumentException("Invalid token type. Expected a refresh token.");
            }

            // Extract the user (subject) from token claims
            String matricule = jwt.getSubject();
            if (matricule == null || matricule.isBlank()) {
                throw new IllegalArgumentException("Invalid refresh token: missing subject");
            }

            // Find user in the database
            UserAccount user = userRepository.findByMatricule(matricule)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for refresh token"));

            // Verify account is still enabled
            if (!user.isEnabled()) {
                throw new IllegalStateException("User account is disabled");
            }

            // Build and return new AuthResponse (with new access & refresh tokens)
            log.info("✅ Refresh token validated for user: {}", matricule);
            return buildAuthResponse(user);

        } catch (JwtException e) {
            log.error("❌ Invalid or expired refresh token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid or expired refresh token", e);
        }
    }

    // ==================== toggleAccountStatus ====================
    
    @Transactional
    public void toggleAccountStatus(String matricule, boolean enabled) {
        if (matricule == null || matricule.isBlank()) {
            throw new IllegalArgumentException("Matricule cannot be blank");
        }

        // Find the user
        UserAccount user = userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new IllegalArgumentException("User not found with matricule: " + matricule));

        // Update status
        user.setEnabled(enabled);
        userRepository.save(user);

        String status = enabled ? "enabled" : "disabled";
        log.info("✅ User '{}' account status changed to {}", matricule, status);
    }


    // ==================== PASSWORD MANAGEMENT ====================

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

    // ==================== ACCOUNT BUILDERS ====================

    private UserAccount buildPatientAccount(PatientRegisterRequest req) {
        UserAccount account = new UserAccount();

        // Determine if patient is minor based on birth date
        boolean minor = isMinor(req.dateNaissance());

        // Use CIN for adults as matricule, else generate a random ID for minors
        if (!minor && req.cin() != null && !req.cin().isBlank()) {
            account.setMatricule(req.cin());
            account.setCIN(req.cin());
        } else if (minor) {
            account.setMatricule("MINOR-" + java.util.UUID.randomUUID());
            account.setCIN(null); // minors don't have CIN
        } else {
            account.setMatricule("PATIENT-" + java.util.UUID.randomUUID());
            account.setCIN(null);
        }

        account.setEmail(req.email());
        account.setPasswordHash(passwordEncoder.encode(req.password()));
        account.setRoles(Set.of(Role.PATIENT));
        account.setEnabled(true);

        // Patient personal info
        account.setFirstName(req.firstName());
        account.setLastName(req.lastName());
        account.setDateNaissance(req.dateNaissance());
        account.setNumeroTelephone(req.numeroTelephone());
        account.setAdresse(req.adresse());
        account.setNumeroSecuriteSociale(req.numeroSecuriteSociale());
        account.setParentCin(req.parentCin());

        return account;
    }
    private UserAccount buildEmployeeAccount(RegisterRequest req, Set<Role> roles) {
        UserAccount account = new UserAccount();
        account.setMatricule(req.matricule());
        account.setEmail(req.email());
        account.setPasswordHash(passwordEncoder.encode(req.password()));
        account.setFirstName(req.firstName());
        account.setLastName(req.lastName());
        account.setRoles(roles);
        account.setEnabled(true);
        account.setExternalId(defaultIfNull(req.externalId(), req.matricule()));

        account.setAdresse(req.address());
        account.setNumeroTelephone(req.phone());

        return account;
    }

    // ==================== EVENT PUBLISHERS ====================

    private void publishPatientCreatedEvent(UserAccount user) {
        try {
            String fullName = buildFullName(user.getFirstName(), user.getLastName());
            boolean minor = isMinor(user.getDateNaissance());

            patientEventPublisher.publishPatientCreatedEvent(
                    user.getId().toString(),
                    fullName,
                    user.getEmail(),
                    user.getCIN(),
                    user.getDateNaissance(),
                    minor,
                    user.getParentCin(),
                    user.getNumeroTelephone(),
                    user.getAdresse(),
                    user.getNumeroSecuriteSociale()
            );

            log.info("✅ Sent PatientCreatedEvent for patient: {} (CIN: {})", fullName, user.getCIN());
        } catch (Exception e) {
            log.error("❌ Failed to publish PatientCreatedEvent for {}", user.getMatricule(), e);
            // Don't throw - allow registration to complete even if event fails
        }
    }

    /**
     * Build full name from first and last names, handling nulls
     */
    private String buildFullName(String firstName, String lastName) {
        String first = (firstName != null && !firstName.isBlank()) ? firstName.trim() : "";
        String last = (lastName != null && !lastName.isBlank()) ? lastName.trim() : "";
        
        String fullName = (first + " " + last).trim();
        return fullName.isEmpty() ? "Unknown Patient" : fullName;
    }


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
        log.debug("📤 Published EmployeeCreatedEvent for matricule: {}", user.getMatricule());
    }

    // ==================== UTILITIES ====================

    private boolean isMinor(LocalDate dateNaissance) {
        if (dateNaissance == null) {
            return false;
        }
        return java.time.Period.between(dateNaissance, java.time.LocalDate.now()).getYears() < 18;
    }

    private UserAccount findUserByMatricule(String matricule) {
        return userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void validateUniqueCredentials(String matricule, String email) {
        if (matricule != null && userRepository.existsByMatricule(matricule)) {
            throw new IllegalArgumentException("Matricule already in use");
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
    }

    private void validateNotNull(Object obj, String name) {
        if (obj == null) throw new IllegalArgumentException(name + " cannot be null");
    }

    private void validateNotBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " cannot be blank");
        }
    }

    private Set<Role> filterEmployeeRoles(Set<Role> requestedRoles) {
        return requestedRoles.stream()
                .filter(role -> role != Role.PATIENT)
                .collect(Collectors.toSet());
    }

    private Set<String> extractRoleNames(UserAccount user) {
        return user.getRoles() == null
                ? Collections.emptySet()
                : user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
    }

    private String defaultIfNull(String value, String def) {
        return value != null ? value : def;
    }

    private void validateEmployeeRequest(RegisterRequest request) {
        if (request.roles() == null || request.roles().isEmpty()) {
            throw new IllegalArgumentException("Employee must have at least one role");
        }
        if (request.roles().contains(Role.PATIENT)) {
            throw new IllegalArgumentException("Employees cannot have PATIENT role");
        }
    }

    private void validateEmployeeCredentials(UserAccount user, String password) {
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    private void validatePatientCredentials(UserAccount user, String password) {
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    private AuthResponse buildAuthResponse(UserAccount user) {
        Instant now = Instant.now();
        Set<String> roleNames = extractRoleNames(user);

        String accessToken = generateAccessToken(user, roleNames, now);
        String refreshToken = generateRefreshToken(user, now);
        String dashboardRoute = user.getRoles().isEmpty()
                ? "/dashboard/default"
                : dashboardService.getDashboardRoute(user.getRoles().iterator().next());
        boolean minor = isMinor(user.getDateNaissance());

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getMatricule(),
                user.getEmail(),
                user.getCIN(),
                user.getRoles(),
                dashboardRoute,
                user.getFirstName(),
                user.getLastName(),
                user.getDateNaissance(),
                user.getNumeroTelephone(),
                user.getAdresse(),
                user.getNumeroSecuriteSociale(),
                minor
        );
    }

    private String generateAccessToken(UserAccount user, Set<String> roles, Instant now) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(TOKEN_ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_MINUTES, ChronoUnit.MINUTES))
                .subject(user.getMatricule())
                .claim("roles", roles)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String generateRefreshToken(UserAccount user, Instant now) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(TOKEN_ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS))
                .subject(user.getMatricule())
                .claim("type", REFRESH_TOKEN_TYPE)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
