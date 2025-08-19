// auth-service/src/main/java/com/hospital/auth/dto/RegisterRequest.java
package com.hospital.auth.dto;

import com.hospital.auth.entity.Role;
import jakarta.validation.constraints.*;
import java.util.Set;

public record RegisterRequest(
  @NotBlank String username,
  @Email String email,
  @NotBlank String password,
  Set<Role> roles,
  String externalId
) {}
