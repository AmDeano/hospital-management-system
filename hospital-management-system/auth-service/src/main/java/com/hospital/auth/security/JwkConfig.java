// auth-service/src/main/java/com/hospital/auth/security/JwkConfig.java
package com.hospital.auth.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.*;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class JwkConfig {

  private final RSAKey rsaJwk;

  public JwkConfig() throws Exception {
    // Dev: generate on startup. In prod, load from env/keystore.
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair kp = kpg.generateKeyPair();

    this.rsaJwk = new RSAKey.Builder((RSAPublicKey) kp.getPublic())
        .privateKey(kp.getPrivate())
        .keyUse(KeyUse.SIGNATURE)
        .algorithm(JWSAlgorithm.RS256)
        .keyID(UUID.randomUUID().toString())
        .build();
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    JWKSet jwkSet = new JWKSet(rsaJwk.toPublicJWK());
    return (jwkSelector, context) -> jwkSelector.select(jwkSet);
  }

  @Bean
  public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
    return new NimbusJwtEncoder(jwkSource);
  }

  @Bean
  public JwtDecoder jwtDecoder() throws Exception {
    return NimbusJwtDecoder.withPublicKey(rsaJwk.toRSAPublicKey()).build();
  }

  public JWKSet jwkSet() { return new JWKSet(rsaJwk.toPublicJWK()); }
}
