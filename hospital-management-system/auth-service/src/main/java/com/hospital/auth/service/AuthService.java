// auth-service/src/main/java/com/hospital/auth/service/AuthService.java
package com.hospital.auth.service;

import com.hospital.auth.dto.*;
import com.hospital.auth.entity.*;
import com.hospital.auth.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;

  public AuthService(UserRepository users, PasswordEncoder encoder, JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
    this.users = users; this.encoder = encoder; this.jwtEncoder = jwtEncoder; this.jwtDecoder = jwtDecoder;
  }

  public void register(RegisterRequest req) {
    if (users.existsByUsername(req.username())) throw new RuntimeException("Username taken");
    var account = new UserAccount();
    account.setUsername(req.username());
    account.setEmail(req.email());
    account.setPasswordHash(encoder.encode(req.password()));
    account.setRoles(req.roles()==null || req.roles().isEmpty() ? Set.of(Role.PATIENT) : req.roles());
    account.setExternalId(req.externalId());
    users.save(account);
  }

  public AuthResponse login(LoginRequest req) {
    var user = users.findByUsername(req.username()).orElseThrow(() -> new RuntimeException("Bad credentials"));
    if (!user.isEnabled() || !encoder.matches(req.password(), user.getPasswordHash()))
      throw new RuntimeException("Bad credentials");

    return buildTokens(user);
  }

  public AuthResponse refresh(String refreshToken) {
	  Jwt jwt = jwtDecoder.decode(refreshToken);   // ✅ use injected decoder
	    var user = users.findByUsername(jwt.getSubject()).orElseThrow();
	    return buildTokens(user);
  }

  private AuthResponse buildTokens(UserAccount user) {
    var now = Instant.now();
    var roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());

    var accessClaims = JwtClaimsSet.builder()
      .issuer("auth-service")
      .issuedAt(now)
      .expiresAt(now.plus(15, ChronoUnit.MINUTES))
      .subject(user.getUsername())
      .claim("uid", user.getId())
      .claim("email", user.getEmail())
      .claim("roles", roles)
      .claim("ext", user.getExternalId())
      .build();

    String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();

    var refreshClaims = JwtClaimsSet.builder()
      .issuer("auth-service")
      .issuedAt(now)
      .expiresAt(now.plus(7, ChronoUnit.DAYS))
      .subject(user.getUsername())
      .claim("type","refresh")
      .build();

    String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

    return new AuthResponse(accessToken, refreshToken, "Bearer", accessClaims.getExpiresAt(), roles, user.getId(), user.getUsername(), user.getEmail());
  }
}
