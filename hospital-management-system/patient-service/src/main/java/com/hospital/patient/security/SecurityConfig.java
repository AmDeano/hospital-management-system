// patient-service/src/main/java/com/hospital/patient/security/SecurityConfig.java
package com.hospital.patient.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable());
    http.authorizeHttpRequests(auth -> auth
      .requestMatchers("/patient-service/actuator/**", "/patient-service/health", "/patient-service/h2-console/**").permitAll()
      .requestMatchers("/patient-service/api/patients/**").hasAnyRole("DOCTOR","NURSE","ADMIN")
      .anyRequest().authenticated()
    );

    http.oauth2ResourceServer(oauth -> oauth
      .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
    );

    return http.build();
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    var conv = new JwtAuthenticationConverter();
    var granted = new JwtGrantedAuthoritiesConverter();
    granted.setAuthoritiesClaimName("roles");  // from our tokens
    granted.setAuthorityPrefix("ROLE_");

    conv.setJwtGrantedAuthoritiesConverter(jwt -> {
      Collection<org.springframework.security.core.GrantedAuthority> authorities = granted.convert(jwt);
      return authorities;
    });

    return conv;
  }
}
