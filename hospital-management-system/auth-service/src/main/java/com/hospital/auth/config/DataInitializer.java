// auth-service/src/main/java/com/hospital/auth/config/DataInitializer.java
package com.hospital.auth.config;

import com.hospital.auth.entity.Role;
import com.hospital.auth.entity.UserAccount;
import com.hospital.auth.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.enabled:false}")
    private boolean initializationEnabled;

    @Value("${app.init.admin-password:}")
    private String adminPassword;

    @Value("${app.init.hr-password:}")
    private String hrPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!initializationEnabled) {
            logger.info("Data initialization is disabled (app.init.enabled=false)");
            return;
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                "ADMIN_PASSWORD environment variable or app.init.admin-password is not set"
            );
        }

        if (hrPassword == null || hrPassword.isBlank()) {
            throw new IllegalStateException(
                "HR_PASSWORD environment variable or app.init.hr-password is not set"
            );
        }

        initializeDefaultAdmin();
        initializeDefaultHR();
    }

    private void initializeDefaultAdmin() {
        String adminUsername = "admin";

        if (!userRepository.existsByMatricule(adminUsername)) {
            UserAccount admin = new UserAccount();
            admin.setMatricule(adminUsername);
            admin.setEmail("admin@hospital.com");
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRoles(Set.of(Role.ADMIN));
            admin.setEnabled(true);
            admin.setExternalId("ADM001");

            userRepository.save(admin);
            logger.info("✅ Default admin user created: {}", adminUsername);
        } else {
            logger.info("ℹ️  Admin user already exists: {}", adminUsername);
        }
    }

    private void initializeDefaultHR() {
        String hrUsername = "hr";

        if (!userRepository.existsByMatricule(hrUsername)) {
            UserAccount hr = new UserAccount();
            hr.setMatricule(hrUsername);
            hr.setEmail("hr@hospital.com");
            hr.setPasswordHash(passwordEncoder.encode(hrPassword));
            hr.setRoles(Set.of(Role.HR));
            hr.setEnabled(true);
            hr.setExternalId("HR001");

            userRepository.save(hr);
            logger.info("✅ Default HR user created: {}", hrUsername);
        } else {
            logger.info("ℹ️  HR user already exists: {}", hrUsername);
        }
    }
}
