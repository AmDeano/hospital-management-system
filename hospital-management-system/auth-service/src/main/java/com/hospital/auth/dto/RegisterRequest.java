// auth-service/src/main/java/com/hospital/auth/dto/RegisterRequest.java
package com.hospital.auth.dto;

import com.hospital.auth.entity.Role;
import jakarta.validation.constraints.*;

import java.util.Set;

public record RegisterRequest(
    @NotBlank String matricule,       // used as username
    @Email String email,
    @NotBlank String password,
    @NotBlank String firstName,       // NEW
    @NotBlank String lastName,        // NEW
    Set<Role> roles,
    String externalId
) {

	public String getMatricule() {
		return matricule;
	}

	public String getEmail() {
		return email;
	}

	public CharSequence getPassword() {
		return password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

}
