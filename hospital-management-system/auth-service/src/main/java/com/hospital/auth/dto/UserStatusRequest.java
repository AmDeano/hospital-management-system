// auth-service/src/main/java/com/hospital/auth/dto/UserStatusRequest.java
package com.hospital.auth.dto;

import jakarta.validation.constraints.NotNull;

public record UserStatusRequest(
    @NotNull(message = "Enabled status is required")
    Boolean enabled
) {}
