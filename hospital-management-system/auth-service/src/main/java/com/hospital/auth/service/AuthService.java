package com.hospital.auth.service;

import com.hospital.auth.config.RabbitConfig;
import com.hospital.auth.dto.*;
import com.hospital.auth.entity.*;
import com.hospital.auth.repo.UserRepository;
import com.hospital.common.events.EmployeeCreatedEvent;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final DashboardService dashboardService;
    private final RabbitTemplate rabbitTemplate;

    public AuthService(UserRepository users,
                       PasswordEncoder encoder,
                       JwtEncoder jwtEncoder,
                       JwtDecoder jwtDecoder,
                       DashboardService dashboardService,
                       RabbitTemplate rabbitTemplate) {
        this.users = users;
        this.encoder = encoder;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.dashboardService = dashboardService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Patients can self-register; role is always PATIENT (client-supplied role ignored).
     */
    public void registerPatient(PatientRegisterRequest req) {
        if (users.existsByMatricule(req.username())) {
            throw new RuntimeException("Username already taken");
        }
        if (req.email() != null && users.existsByEmail(req.email())) {
            throw new RuntimeException("Email already taken");
        }

        var account = new UserAccount();
        account.setMatricule(req.username());
        account.setEmail(req.email());
        account.setPasswordHash(encoder.encode(req.password()));
        account.setRoles(Set.of(Role.PATIENT));
        account.setExternalId(req.externalId());
        users.save(account);
    }

    /**
     * Register employee - Only for HR/Admin use
     */
    public void registerEmployee(RegisterRequest req) {
        validateEmployeeRegistration(req);

        if (users.existsByMatricule(req.matricule())) {
            throw new RuntimeException("Username already taken");
        }
        if (req.email() != null && users.existsByEmail(req.email())) {
            throw new RuntimeException("Email already taken");
        }

        // Ensure no PATIENT role is included for employees
        Set<Role> employeeRoles = req.roles().stream()
                .filter(role -> role != Role.PATIENT)
                .collect(Collectors.toSet());

        if (employeeRoles.isEmpty()) {
            throw new RuntimeException("At least one employee role must be specified");
        }

        var account = new UserAccount();
        account.setMatricule(req.matricule());
        account.setEmail(req.email());
        account.setPasswordHash(encoder.encode(req.password()));
        account.setFirstName(req.firstName());  
        account.setLastName(req.lastName());
        account.setRoles(employeeRoles);
        account.setExternalId(req.externalId());


        UserAccount saved = users.save(account);
        
        publishEmployeeCreatedEvent(saved);
    }

    /**
     * Register admin - Only for existing admins
     */
    public void registerAdmin(RegisterRequest req) {
        if (users.existsByMatricule(req.matricule())) {
            throw new RuntimeException("Username already taken");
        }
        if (req.email() != null && users.existsByEmail(req.email())) {
            throw new RuntimeException("Email already taken");
        }

        var account = new UserAccount();
        account.setMatricule(req.matricule());
        account.setEmail(req.email());
        account.setPasswordHash(encoder.encode(req.password()));
        account.setRoles(Set.of(Role.ADMIN));
        account.setFirstName(req.firstName());  // ADD THIS
        account.setLastName(req.lastName());    // ADD THIS
        account.setExternalId(req.externalId());
        UserAccount saved = users.save(account);

        // PUBLISH EVENT TO EMPLOYEE-SERVICE
        publishEmployeeCreatedEvent(saved);
    }
    
 // ADD THIS METHOD
    private void publishEmployeeCreatedEvent(UserAccount user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        EmployeeCreatedEvent event = new EmployeeCreatedEvent(
            user.getId(),
            user.getMatricule(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            null, // passwordHash not sent for security
            roleNames,
            user.getExternalId()
        );

        rabbitTemplate.convertAndSend(RabbitConfig.EMPLOYEE_CREATED_QUEUE, event);
        System.out.println("📤 Published EmployeeCreatedEvent for: " + user.getMatricule());
    }
    /**
     * Employees can log in; must have a non-PATIENT role.
     */
    public AuthResponse loginEmployee(LoginRequest req) {
        var user = users.findByMatricule(req.matricule())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        var hasEmployeeRole = user.getRoles() != null &&
                user.getRoles().stream().anyMatch(r -> r != Role.PATIENT);

        if (!user.isEnabled() ||
            !encoder.matches(req.password(), user.getPasswordHash()) ||
            !hasEmployeeRole) {
            throw new RuntimeException("Invalid credentials");
        }

        return buildTokens(user);
    }

    /**
     * Patient login - separate endpoint for patients
     */
    public AuthResponse loginPatient(LoginRequest req) {
        var user = users.findByMatricule(req.matricule())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        var isPatient = user.getRoles() != null &&
                user.getRoles().contains(Role.PATIENT);

        if (!user.isEnabled() ||
            !encoder.matches(req.password(), user.getPasswordHash()) ||
            !isPatient) {
            throw new RuntimeException("Invalid credentials");
        }

        return buildTokens(user);
    }

    /**
     * Refresh access token using refresh JWT.
     */
    public AuthResponse refresh(String refreshToken) {
        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);

            // Verify this is a refresh token
            String tokenType = jwt.getClaimAsString("type");
            if (!"refresh".equals(tokenType)) {
                throw new RuntimeException("Invalid token type");
            }

            var user = users.findByMatricule(jwt.getSubject())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.isEnabled()) {
                throw new RuntimeException("User account is disabled");
            }

            return buildTokens(user);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid refresh token", e);
        }
    }

    /**
     * Change password for authenticated user
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        var user = users.findByMatricule(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Invalid current password");
        }

        user.setPasswordHash(encoder.encode(newPassword));
        users.save(user);
    }

    /**
     * Enable/Disable user account
     */
    public void toggleUserAccount(String username, boolean enabled) {
        var user = users.findByMatricule(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(enabled);
        users.save(user);
    }

    private void validateEmployeeRegistration(RegisterRequest req) {
        if (req.roles() == null || req.roles().isEmpty()) {
            throw new RuntimeException("At least one role must be specified for employee");
        }

        // Check if trying to assign PATIENT role to employee
        if (req.roles().contains(Role.PATIENT)) {
            throw new RuntimeException("PATIENT role cannot be assigned to employees");
        }

        // Validate role combinations
        boolean hasAdmin = req.roles().contains(Role.ADMIN);
        boolean hasOtherRoles = req.roles().stream()
                .anyMatch(role -> role != Role.ADMIN);

        if (hasAdmin && hasOtherRoles) {
            throw new RuntimeException("ADMIN role cannot be combined with other roles");
        }
    }

    private AuthResponse buildTokens(UserAccount user) {
        Instant now = Instant.now();

        // Build roles as strings
        Set<String> roleNames = user.getRoles() == null
                ? Set.of()
                : user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());

        // Build access token
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .subject(user.getMatricule())
                .claim("uid", user.getId())
                .claim("email", user.getEmail())
                .claim("roles", roleNames)
                .claim("ext", user.getExternalId())
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();

        // Build refresh token
        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(now.plus(7, ChronoUnit.DAYS))
                .subject(user.getMatricule())
                .claim("type", "refresh")
                .build();

        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

        // Determine dashboard route
        // Pick “main” role by priority
        Role mainRole = user.getRoles().stream()
                .min((r1, r2) -> rolePriority(r1) - rolePriority(r2))
                .orElse(Role.PATIENT);

        String dashboard = dashboardService.getDashboardRoute(mainRole);

        // return response
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getMatricule(),
                user.getEmail(),
                user.getExternalId(),
                user.getRoles(),
                dashboard
        );
    }

    private int rolePriority(Role role) {
        // smaller number = higher priority
        return switch (role) {
            case ADMIN -> 1;
            case HR -> 2;
            case DOCTOR -> 3;
            case NURSE -> 4;
            case RECEPTIONIST -> 5;
            case SUPERVISOR -> 6;
            case OBSERVATOR -> 6;
            case PATIENT -> 7;
            default -> 99;
        };
    }
}
