package com.hospital.employee.controller;

import com.hospital.employee.dto.DoctorDashboardDto;
import com.hospital.employee.dto.DoctorDto;
import com.hospital.employee.dto.DoctorAvailabilitySlotDto;
import com.hospital.employee.service.DoctorDashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Doctor Dashboard Controller
 * Provides endpoints for doctor functionalities and dashboard
 * Accessible via: /dashboard/doctors/{doctorMatricule}
 */
@RestController
@RequestMapping("/dashboard/doctors")
@PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
public class DoctorDashboardController {

    private final DoctorDashboardService dashboardService;

    public DoctorDashboardController(DoctorDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * GET /dashboard/doctors/{doctorMatricule}
     * Get complete doctor dashboard with all functionalities
     */
    @GetMapping("/{doctorMatricule}")
    public ResponseEntity<DoctorDashboardDto> getDoctorDashboard(@PathVariable String doctorMatricule) {
        return ResponseEntity.ok(dashboardService.getDoctorDashboard(doctorMatricule));
    }

    /**
     * GET /dashboard/doctors/{doctorMatricule}/info
     * Get doctor's personal information
     */
    @GetMapping("/{doctorMatricule}/info")
    public ResponseEntity<DoctorDto> getDoctorInfo(@PathVariable String doctorMatricule) {
        return ResponseEntity.ok(dashboardService.getDoctorInfo(doctorMatricule));
    }

    /**
     * GET /dashboard/doctors/{doctorMatricule}/available-slots
     * Get available appointment slots for a specific date
     * Query param: date (yyyy-MM-dd format)
     */
    @GetMapping("/{doctorMatricule}/available-slots")
    public ResponseEntity<List<DoctorAvailabilitySlotDto>> getAvailableSlots(
            @PathVariable String doctorMatricule,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(dashboardService.getAvailableSlots(doctorMatricule, date));
    }
}
