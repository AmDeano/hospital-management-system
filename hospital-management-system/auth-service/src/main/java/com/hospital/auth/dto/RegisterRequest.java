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
    String phone,
    String address,
    Set<Role> roles,
    String externalId,
    
    //Doc fields
    String specialization,
    String licenseNumber,
    String medicalDegree,
    
    //Nurse fiel
    String shift,
    String nurseLicenseNumber,
    
    //Recep fields
    String deskNumber,
    
    //Admin fields
    String departmentArea,
    
    //Observator fields
    String assignedArea
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

	public String getPhone() {
		return phone;
	}
	public String getAddress() {
		return address;
	}
}
