package com.hospital.employee.controller;

import com.hospital.employee.entity.Nurse;
import com.hospital.employee.usecase.NurseUseCaseImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee-service/api/nurses")
@PreAuthorize("hasRole('ADMIN') or hasRole('NURSE') or hasRole('SUPERVISOR')")
public class NurseController {

    private final NurseUseCaseImpl nurseUseCase;

    public NurseController(NurseUseCaseImpl nurseUseCase) {
        this.nurseUseCase = nurseUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Nurse> create(@Valid @RequestBody Nurse nurse) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nurseUseCase.create(nurse));
    }

    @GetMapping
    public ResponseEntity<List<Nurse>> all() {
        return ResponseEntity.ok(nurseUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nurse> get(@PathVariable Long id) {
        return ResponseEntity.ok(nurseUseCase.findById(id));
    }

    @GetMapping("/shift/{shift}")
    public ResponseEntity<List<Nurse>> getByShift(@PathVariable String shift) {
        return ResponseEntity.ok(nurseUseCase.findByShift(shift));
    }

    @GetMapping("/shift/{shift}/active")
    public ResponseEntity<List<Nurse>> getActiveByShift(@PathVariable String shift) {
        return ResponseEntity.ok(nurseUseCase.findActiveByShift(shift));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Nurse> update(@PathVariable Long id, @Valid @RequestBody Nurse nurse) {
        return ResponseEntity.ok(nurseUseCase.update(id, nurse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        nurseUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
