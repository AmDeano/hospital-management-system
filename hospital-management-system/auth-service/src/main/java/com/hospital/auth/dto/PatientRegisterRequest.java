// auth-service/src/main/java/com/hospital/auth/dto/PatientRegisterRequest.java
package com.hospital.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Patients can self-register; role is always enforced to PATIENT. */
public record PatientRegisterRequest(
  @NotBlank String username,       // could be email too
  @Email String email,
  @NotBlank String password,
  String externalId                // e.g., CIN
) {}
