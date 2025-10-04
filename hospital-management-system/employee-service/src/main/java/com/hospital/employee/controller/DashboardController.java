package com.hospital.employee.controller;

import com.hospital.employee.usecase.DetermineDashboardRouteUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardController {
    private final DetermineDashboardRouteUseCase determineDashboardRouteUseCase;

    @Autowired
    public DashboardController(DetermineDashboardRouteUseCase determineDashboardRouteUseCase) {
        this.determineDashboardRouteUseCase = determineDashboardRouteUseCase;
    }

    @GetMapping("/api/dashboard-route")
    public Map<String, String> getDashboardRoute(Authentication authentication) {
        String route = determineDashboardRouteUseCase.execute(authentication.getAuthorities());
        return Map.of("dashboardRoute", route);
    }
}
