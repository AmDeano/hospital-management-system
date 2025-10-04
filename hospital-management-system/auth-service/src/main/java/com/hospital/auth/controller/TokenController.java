// auth-service/src/main/java/com/hospital/auth/controller/TokenController.java
package com.hospital.auth.controller;

import com.hospital.auth.dto.AuthResponse;
import com.hospital.auth.dto.ChangePasswordRequest;
import com.hospital.auth.dto.UserStatusRequest;
import com.hospital.auth.entity.UserAccount;
import com.hospital.auth.repo.UserRepository;
import com.hospital.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class TokenController {
    private final AuthService authService;
    private final UserRepository userRepository;

    public TokenController(AuthService authService, UserRepository userRepository) {
        this.authService = authService; 
        this.userRepository = userRepository;
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String refreshToken){
        return authService.refresh(refreshToken);
    }

    @GetMapping("/me")
    public UserAccount me(@AuthenticationPrincipal Jwt jwt){
        return userRepository.findByMatricule(jwt.getSubject()).orElseThrow();
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid ChangePasswordRequest request) {
        authService.changePassword(jwt.getSubject(), request.currentPassword(), request.newPassword());
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/users/{username}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> toggleUserStatus(
            @PathVariable String username,
            @RequestBody @Valid UserStatusRequest request) {
        authService.toggleUserAccount(username, request.enabled());
        String status = request.enabled() ? "enabled" : "disabled";
        return ResponseEntity.ok("User " + username + " has been " + status);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok("Token is valid for user: " + jwt.getSubject());
    }
}