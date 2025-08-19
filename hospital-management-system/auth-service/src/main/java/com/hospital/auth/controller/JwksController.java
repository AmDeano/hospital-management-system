// auth-service/src/main/java/com/hospital/auth/controller/JwksController.java
package com.hospital.auth.controller;

import com.hospital.auth.security.JwkConfig;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class JwksController {
  private final JwkConfig jwkConfig;
  public JwksController(JwkConfig jwkConfig) { this.jwkConfig = jwkConfig; }

  @GetMapping(value="/.well-known/jwks.json", produces=MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> jwks() {
    return new JWKSet(jwkConfig.jwkSet().getKeys()).toJSONObject();
  }
}
