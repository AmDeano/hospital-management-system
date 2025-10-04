package com.hospital.employee.controller;

import com.hospital.employee.entity.AdministrativeStaff;
import com.hospital.employee.usecase.AdministrativeStaffUseCaseImpl;
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

    private final AdministrativeStaffUseCaseImpl staffUseCase;

    public AdministrativeStaffController(AdministrativeStaffUseCaseImpl staffUseCase) {
        this.staffUseCase = staffUseCase;
    }


    @GetMapping
    public ResponseEntity<List<AdministrativeStaff>> all() {
        return ResponseEntity.ok(staffUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrativeStaff> get(@PathVariable Long id) {
        return ResponseEntity.ok(staffUseCase.findById(id));
    }

    @GetMapping("/area/{area}")
    public ResponseEntity<List<AdministrativeStaff>> getByArea(@PathVariable String area) {
        return ResponseEntity.ok(staffUseCase.findByDepartmentArea(area));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministrativeStaff> update(@PathVariable Long id, @Valid @RequestBody AdministrativeStaff staff) {
        return ResponseEntity.ok(staffUseCase.update(id, staff));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        staffUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
