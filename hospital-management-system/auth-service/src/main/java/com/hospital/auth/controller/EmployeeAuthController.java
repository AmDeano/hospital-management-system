// auth-service/src/main/java/com/hospital/auth/controller/EmployeeAuthController.java
package com.hospital.auth.controller;

import com.hospital.auth.dto.AuthResponse;
import com.hospital.auth.dto.LoginRequest;
import com.hospital.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/employee")
public class EmployeeAuthController {
  private final AuthService auth;

  public EmployeeAuthController(AuthService auth) { this.auth = auth; }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody @Valid LoginRequest req) {
    return auth.loginEmployee(req);
  }
}
