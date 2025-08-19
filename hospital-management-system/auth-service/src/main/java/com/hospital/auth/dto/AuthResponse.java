// auth-service/src/main/java/com/hospital/auth/dto/AuthResponse.java
package com.hospital.auth.dto;

import java.time.Instant;
import java.util.Set;

public record AuthResponse(String accessToken, String refreshToken, String tokenType, Instant expiresAt, Set<String> roles, Long userId, String username, String email) {}
