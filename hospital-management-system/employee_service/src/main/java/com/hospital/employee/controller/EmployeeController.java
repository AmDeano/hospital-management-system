package com.hospital.employee.controller;

import com.hospital.employee.dto.EmployeeDto;
import com.hospital.employee.entity.EmployeeType;
import com.hospital.employee.entity.WorkDay;
import com.hospital.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Basic CRUD Operations
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto dto) {
        return ResponseEntity.status(201).body(employeeService.createEmployee(dto));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{matricule}")
    public ResponseEntity<EmployeeDto> getEmployeeByMatricule(@PathVariable String matricule) {
        return ResponseEntity.ok(employeeService.getEmployeeByMatricule(matricule));
    }

    @PutMapping("/{matricule}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable String matricule, 
                                                    @Valid @RequestBody EmployeeDto dto) {
        return ResponseEntity.ok(employeeService.updateEmployee(matricule, dto));
    }

    @DeleteMapping("/{matricule}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String matricule) {
        employeeService.deleteEmployee(matricule);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{matricule}/activate")
    public ResponseEntity<EmployeeDto> activateEmployee(@PathVariable String matricule) {
        return ResponseEntity.ok(employeeService.activateEmployee(matricule));
    }

    // Employee Type Based Queries
    @GetMapping("/type/{employeeType}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByType(@PathVariable EmployeeType employeeType) {
        return ResponseEntity.ok(employeeService.getEmployeesByType(employeeType));
    }

    @GetMapping("/administration")
    public ResponseEntity<List<EmployeeDto>> getAdministrationEmployees() {
        return ResponseEntity.ok(employeeService.getAdministrationEmployees());
    }

    @GetMapping("/medical-staff")
    public ResponseEntity<List<EmployeeDto>> getMedicalStaff() {
        return ResponseEntity.ok(employeeService.getMedicalStaff());
    }

    // Department Based Queries
    @GetMapping("/department/{departement}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartment(@PathVariable String departement) {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(departement));
    }

    @GetMapping("/department/{departement}/type/{employeeType}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartmentAndType(
            @PathVariable String departement, 
            @PathVariable EmployeeType employeeType) {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartmentAndType(departement, employeeType));
    }

    // Medical Staff Specific Queries
    @GetMapping("/medical-staff/speciality/{specialite}")
    public ResponseEntity<List<EmployeeDto>> getMedicalStaffBySpeciality(@PathVariable String specialite) {
        return ResponseEntity.ok(employeeService.getMedicalStaffBySpeciality(specialite));
    }

    @GetMapping("/doctors/available/{workDay}")
    public ResponseEntity<List<EmployeeDto>> getAvailableDoctorsByWorkDay(@PathVariable WorkDay workDay) {
        return ResponseEntity.ok(employeeService.getAvailableDoctorsByWorkDay(workDay));
    }

    // Supervisor and Hierarchy Queries
    @GetMapping("/supervisor/{supervisorMatricule}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesBySupervisor(@PathVariable String supervisorMatricule) {
        return ResponseEntity.ok(employeeService.getEmployeesBySupervisor(supervisorMatricule));
    }

    // Status Based Queries
    @GetMapping("/active")
    public ResponseEntity<List<EmployeeDto>> getActiveEmployees() {
        return ResponseEntity.ok(employeeService.getActiveEmployees());
    }

    // Search Operations
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDto>> searchEmployees(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String departement,
            @RequestParam(required = false) EmployeeType employeeType,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(employeeService.searchEmployees(nom, prenom, departement, employeeType, isActive));
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<EmployeeDto>> searchEmployeesByName(@RequestParam String searchTerm) {
        return ResponseEntity.ok(employeeService.searchEmployeesByName(searchTerm));
    }

    // Date Range Queries
    @GetMapping("/hire-date-range")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByHireDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(employeeService.getEmployeesByHireDateRange(startDate, endDate));
    }

    // Statistics and Analytics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getEmployeeStatistics() {
        return ResponseEntity.ok(employeeService.getEmployeeStatistics());
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Employee Service is running!");
    }
}