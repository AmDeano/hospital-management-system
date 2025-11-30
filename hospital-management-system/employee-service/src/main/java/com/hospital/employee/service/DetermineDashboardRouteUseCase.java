package com.hospital.employee.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Component
public class DetermineDashboardRouteUseCase {

    public String execute(Collection<? extends GrantedAuthority> authorities) {
        Optional<String> mainRole = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .map(auth -> auth.startsWith("ROLE_") ? auth.substring(5) : auth)
            .map(String::toUpperCase)
            .min(Comparator.comparingInt(this::rolePriority));

        String role = mainRole.orElse("PATIENT");
        return dashboardRouteFor(role);
    }

    private int rolePriority(String role) {
        return switch (role) {
            case "ADMIN" -> 1;
            case "HR" -> 2;
            case "DOCTOR" -> 3;
            case "NURSE" -> 4;
            case "RECEPTIONIST" -> 5;
            case "STAFF" -> 6;
            case "PATIENT" -> 7;
            default -> 99;
        };
    }

    private String dashboardRouteFor(String role) {
        return switch (role) {
            case "ADMIN" -> "/dashboard/admin";
            case "HR" -> "/dashboard/hr";
            case "DOCTOR" -> "/dashboard/doctor";
            case "NURSE" -> "/dashboard/nurse";
            case "RECEPTIONIST" -> "/dashboard/receptionist";
            case "STAFF" -> "/dashboard/staff";
            case "PATIENT" -> "/dashboard/patient";
            default -> "/dashboard";
        };
    }
}
