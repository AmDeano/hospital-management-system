package com.hospital.auth.controller;

import com.hospital.auth.dto.AuthResponse;
import com.hospital.auth.dto.ChangePasswordRequest;
import com.hospital.auth.dto.UserStatusRequest;
import com.hospital.auth.entity.UserAccount;
import com.hospital.auth.service.AuthService;
import com.hospital.auth.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Handles token operations, user profile, and account management
 */
@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private final AuthService authService;
    private final UserService userService;

    public TokenController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * Refresh access token using a valid refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam @NotBlank String refreshToken) {
        AuthResponse response = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user profile
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAccount> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        UserAccount user = userService.findByMatricule(jwt.getSubject());
        return ResponseEntity.ok(user);
    }

    /**
     * Validate if the current JWT token is valid
     */
    @GetMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TokenValidationResponse> validateToken(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(new TokenValidationResponse(
                true,
                jwt.getSubject(),
                jwt.getExpiresAt()
        ));
    }

    /**
     * Change password for authenticated user
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid ChangePasswordRequest request) {
        
        String matricule = jwt.getSubject();
        authService.changePassword(matricule, request.currentPassword(), request.newPassword());
        
        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }

    /**
     * Enable or disable a user account (Admin only)
     */
    @PutMapping("/users/{matricule}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> toggleAccountStatus(
            @PathVariable @NotBlank String matricule,
            @RequestBody @Valid UserStatusRequest request) {
        
        authService.toggleAccountStatus(matricule, request.enabled());
        
        String action = request.enabled() ? "enabled" : "disabled";
        return ResponseEntity.ok(
                new MessageResponse("User " + matricule + " has been " + action + " successfully")
        );
    }

    /**
     * Response DTOs for consistency
     */
    record MessageResponse(String message) {}
    
    record TokenValidationResponse(
            boolean valid,
            String subject,
            java.time.Instant expiresAt
    ) {}
}