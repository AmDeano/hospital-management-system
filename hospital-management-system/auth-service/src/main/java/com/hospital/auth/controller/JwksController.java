// auth-service/src/main/java/com/hospital/auth/controller/JwksController.java
package com.hospital.auth.controller;

import com.hospital.auth.security.JwkConfig;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JwksController {

    private final JwkConfig jwkConfig;

    public JwksController(JwkConfig jwkConfig) {
        this.jwkConfig = jwkConfig;
    }

    @GetMapping(value = "/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> jwks() {
        // Directly return the JWKSet from config
        return jwkConfig.jwkSet().toJSONObject();
    }
}
