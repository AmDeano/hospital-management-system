// auth-service/src/main/java/com/hospital/auth/dto/LoginRequest.java
package com.hospital.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
  @NotBlank String matricule,
  @NotBlank String password
) {}
