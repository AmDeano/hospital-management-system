package com.hospital.auth.service;

import com.hospital.auth.entity.UserAccount;
import com.hospital.auth.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository is required");
    }

    /**
     * Find user by matricule
     */
    public UserAccount findByMatricule(String matricule) {
        if (matricule == null || matricule.isBlank()) {
            throw new IllegalArgumentException("Matricule cannot be blank");
        }
        
        return userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + matricule));
    }

    /**
     * Find user by email
     */
    public UserAccount findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    /**
     * Check if matricule exists
     */
    public boolean existsByMatricule(String matricule) {
        return matricule != null && userRepository.existsByMatricule(matricule);
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return email != null && userRepository.existsByEmail(email);
    }
}