// auth-service/src/main/java/com/hospital/auth/config/DataInitializer.java
package com.hospital.auth.config;

import com.hospital.auth.entity.Role;
import com.hospital.auth.entity.UserAccount;
import com.hospital.auth.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) throws Exception {
        initializeDefaultAdmin();
        initializeDefaultHR();
    }
    
    private void initializeDefaultAdmin() {
        String adminUsername = "admin";
        
        if (!userRepository.existsByUsername(adminUsername)) {
            UserAccount admin = new UserAccount();
            admin.setUsername(adminUsername);
            admin.setEmail("admin@hospital.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(Role.ADMIN));
            admin.setEnabled(true);
            admin.setExternalId("ADM001");
            
            userRepository.save(admin);
            logger.info("Default admin user created: {}", adminUsername);
        } else {
            logger.info("Admin user already exists: {}", adminUsername);
        }
    }
    
    private void initializeDefaultHR() {
        String hrUsername = "hr";
        
        if (!userRepository.existsByUsername(hrUsername)) {
            UserAccount hr = new UserAccount();
            hr.setUsername(hrUsername);
            hr.setEmail("hr@hospital.com");
            hr.setPasswordHash(passwordEncoder.encode("hr123"));
            hr.setRoles(Set.of(Role.HR));
            hr.setEnabled(true);
            hr.setExternalId("HR001");
            
            userRepository.save(hr);
            logger.info("Default HR user created: {}", hrUsername);
        } else {
            logger.info("HR user already exists: {}", hrUsername);
        }
    }
}
