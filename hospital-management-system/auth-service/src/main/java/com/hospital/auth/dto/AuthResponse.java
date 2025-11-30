package com.hospital.auth.dto;

import java.time.LocalDate;
import java.util.Set;

import com.hospital.auth.entity.Role;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long userId,
    String matricule,
    String email,
    String cin,
    Set<Role> roles,
    String dashboardRoute,
    // Patient-specific fields
    String firstName,
    String lastName,
    LocalDate dateNaissance,
    String numeroTelephone,
    String adresse,
    String numeroSecuriteSociale,
    boolean isMinor
) {}
