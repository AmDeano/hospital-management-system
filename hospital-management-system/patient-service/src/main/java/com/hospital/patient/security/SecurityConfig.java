// patient-service/src/main/java/com/hospital/patient/security/SecurityConfig.java
package com.hospital.patient.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:http://localhost:8083/auth-service/.well-known/jwks.json}")
    private String jwkSetUri;

    // Production/Default Security Configuration
    @Bean
    @Profile("!dev")
    public SecurityFilterChain productionSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/patient-service/actuator/**", "/patient-service/health").permitAll()
            .requestMatchers("/patient-service/h2-console/**").permitAll()
            .requestMatchers("/patient-service/swagger-ui/**", "/patient-service/v3/api-docs/**").permitAll()
            .requestMatchers("/patient-service/error").permitAll()
            
            // Patient endpoints - accessible by multiple roles
            .requestMatchers("/patient-service/api/patients/**").hasAnyRole("DOCTOR", "NURSE", "ADMIN", "RECEPTIONIST", "PATIENT")
            
            .anyRequest().authenticated()
        );

        http.oauth2ResourceServer(oauth -> oauth
            .jwt(jwt -> jwt
                .decoder(jwtDecoder())
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
            )
        );

        http.headers(headers -> headers.frameOptions().sameOrigin());
        return http.build();
    }

    // Development Security Configuration (No Authentication)
    @Bean
    @Profile("dev")
    public SecurityFilterChain developmentSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        
        http.authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        );

        http.headers(headers -> headers.frameOptions().sameOrigin());
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        } catch (Exception e) {
            System.err.println("Failed to configure JWT decoder with JWK Set URI: " + e.getMessage());
            throw new RuntimeException("JWT configuration failed", e);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<org.springframework.security.core.GrantedAuthority> authorities = 
                grantedAuthoritiesConverter.convert(jwt);
            return authorities;
        });
        
        return converter;
    }
}