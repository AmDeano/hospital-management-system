// employee-service/src/main/java/com/hospital/employee/security/SecurityConfig.java
package com.hospital.employee.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
            // actuator/health should remain public
            .requestMatchers("/employee-service/actuator/**", "/employee-service/health").permitAll()

            // Protect employees API
            .requestMatchers("/employee-service/api/employees/**").hasAnyRole("ADMIN", "STAFF", "DOCTOR")

            // everything else must be authenticated
            .anyRequest().authenticated()
        );

        http.oauth2ResourceServer(oauth -> oauth
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter granted = new JwtGrantedAuthoritiesConverter();
        // our tokens have a "roles" claim
        granted.setAuthoritiesClaimName("roles");
        granted.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection authorities = granted.convert(jwt);
            return authorities;
        });
        return converter;
    }
}
