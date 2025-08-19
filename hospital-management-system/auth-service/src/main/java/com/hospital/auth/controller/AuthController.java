// auth-service/src/main/java/com/hospital/auth/controller/AuthController.java
package com.hospital.auth.controller;

import com.hospital.auth.dto.*;
import com.hospital.auth.entity.UserAccount;
import com.hospital.auth.repo.UserRepository;
import com.hospital.auth.service.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService auth;
  private final UserRepository users;

  public AuthController(AuthService auth, UserRepository users) { this.auth = auth; this.users = users; }

  @PostMapping("/register")
  public void register(@RequestBody @Validated RegisterRequest req){ auth.register(req); }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody @Validated LoginRequest req){ return auth.login(req); }

  @PostMapping("/refresh")
  public AuthResponse refresh(@RequestParam String refreshToken){ return auth.refresh(refreshToken); }

  @GetMapping("/me")
  public UserAccount me(@AuthenticationPrincipal Jwt jwt){
    return users.findByUsername(jwt.getSubject()).orElseThrow();
  }
}
