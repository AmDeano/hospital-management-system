package com.hospital.employee.controller;

import com.hospital.employee.entity.Nurse;
import com.hospital.employee.service.NurseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nurses")
@PreAuthorize("hasRole('ADMIN') or hasRole('NURSE') or hasRole('SUPERVISOR')")
public class NurseController {

    private final NurseService nurseService;

    public NurseController(NurseService nurseService) {
        this.nurseService = nurseService;
    }

    @GetMapping
    public ResponseEntity<List<Nurse>> all() {
        return ResponseEntity.ok(nurseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nurse> get(@PathVariable Long id) {
        return ResponseEntity.ok(nurseService.findById(id));
    }

    @GetMapping("/shift/{shift}")
    public ResponseEntity<List<Nurse>> getByShift(@PathVariable String shift) {
        return ResponseEntity.ok(nurseService.findByShift(shift));
    }

    @GetMapping("/shift/{shift}/active")
    public ResponseEntity<List<Nurse>> getActiveByShift(@PathVariable String shift) {
        return ResponseEntity.ok(nurseService.findActiveByShift(shift));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Nurse> update(@PathVariable Long id, @Valid @RequestBody Nurse nurse) {
        return ResponseEntity.ok(nurseService.update(id, nurse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        nurseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
