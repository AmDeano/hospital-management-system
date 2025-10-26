// auth-service/src/main/java/com/hospital/auth/controller/PatientAuthController.java
package com.hospital.auth.controller;

import com.hospital.auth.dto.AuthResponse;
import com.hospital.auth.dto.LoginRequest;
import com.hospital.auth.dto.LoginRequestEmail;
import com.hospital.auth.dto.PatientRegisterRequest;
import com.hospital.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/patient")
@CrossOrigin(origins = "*")
public class PatientAuthController {
    private final AuthService authService;

    public PatientAuthController(AuthService authService) { 
        this.authService = authService; 
    }

    /**
     * Patient self-registration - Open to public
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid PatientRegisterRequest req) {
        authService.registerPatient(req);
    }

    /**
     * Patient login - Separate from employee login
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequestEmail req) {
        return authService.loginPatient(req);
    }
}