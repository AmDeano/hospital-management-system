// auth-service/src/main/java/com/hospital/auth/controller/EmployeeRegistrationController.java
package com.hospital.auth.controller;

import com.hospital.auth.dto.RegisterRequest;
import com.hospital.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/employee")
public class EmployeeRegistrationController {
    private final AuthService authService;

    public EmployeeRegistrationController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register new employee - Only accessible by HR or ADMIN roles
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public void registerEmployee(@RequestBody @Valid RegisterRequest request) {
        authService.registerEmployee(request);
    }

    /**
     * Register admin - Only accessible by existing ADMIN (for initial setup)
     */
    @PostMapping("/register-admin")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public void registerAdmin(@RequestBody @Valid RegisterRequest request) {
        authService.registerAdmin(request);
    }
}