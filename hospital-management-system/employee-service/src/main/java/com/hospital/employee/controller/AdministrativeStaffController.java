package com.hospital.employee.controller;

import com.hospital.employee.entity.AdministrativeStaff;
import com.hospital.employee.service.AdministrativeStaffService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adminstaff")
@PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('SUPERVISOR')")
public class AdministrativeStaffController {

    private final AdministrativeStaffService staffService;

    public AdministrativeStaffController(AdministrativeStaffService staffService) {
        this.staffService = staffService;
    }


    @GetMapping
    public ResponseEntity<List<AdministrativeStaff>> all() {
        return ResponseEntity.ok(staffService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrativeStaff> get(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.findById(id));
    }

    @GetMapping("/area/{area}")
    public ResponseEntity<List<AdministrativeStaff>> getByArea(@PathVariable String area) {
        return ResponseEntity.ok(staffService.findByDepartmentArea(area));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministrativeStaff> update(@PathVariable Long id, @Valid @RequestBody AdministrativeStaff staff) {
        return ResponseEntity.ok(staffService.update(id, staff));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        staffService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
