package com.hospital.employee.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.headers(headers -> headers
            .frameOptions(frame -> frame.disable()) // if needed for H2 console
        );

        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**", "/h2-console/**").permitAll()
            // allow the dashboard route endpoint for authenticated users
            .requestMatchers("/employee/dashboard-route").authenticated()
            // other endpoints: you might put more specific rules
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/hr/**").hasAnyRole("ADMIN", "HR", "SUPERVISOR")
            .requestMatchers("/api/doctor/**").hasAnyRole("ADMIN", "DOCTOR", "SUPERVISOR")
            .requestMatchers("/api/nurse/**").hasAnyRole("ADMIN", "NURSE","DOCTOR", "SUPERVISOR")
            .requestMatchers("/api/receptionist/**").hasAnyRole("ADMIN", "RECEPTIONIST", "SUPERVISOR")
            .requestMatchers("/api/observator/**").hasAnyRole("ADMIN", "OBSERVATOR", "SUPERVISOR")
            .requestMatchers("/api/supervisor/**").hasAnyRole("ADMIN", "SUPERVISOR")
            
            .anyRequest().authenticated()
        );

        http.oauth2ResourceServer(oauth -> oauth
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedConverter = new JwtGrantedAuthoritiesConverter();
        grantedConverter.setAuthoritiesClaimName("roles");
        grantedConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(grantedConverter);
        return authConverter;
    }
}
