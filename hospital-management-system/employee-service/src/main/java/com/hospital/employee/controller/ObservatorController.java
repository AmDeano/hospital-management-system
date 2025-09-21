package com.hospital.employee.controller;

import com.hospital.employee.entity.Observator;
import com.hospital.employee.usecase.ObservatorUseCaseImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee-service/api/observators")
@PreAuthorize("hasRole('ADMIN') or hasRole('OBSERVATOR')")
public class ObservatorController {

    private final ObservatorUseCaseImpl observatorUseCase;

    public ObservatorController(ObservatorUseCaseImpl observatorUseCase) {
        this.observatorUseCase = observatorUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Observator> create(@Valid @RequestBody Observator observator) {
        return ResponseEntity.status(HttpStatus.CREATED).body(observatorUseCase.create(observator));
    }

    @GetMapping
    public ResponseEntity<List<Observator>> all() {
        return ResponseEntity.ok(observatorUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Observator> get(@PathVariable Long id) {
        return ResponseEntity.ok(observatorUseCase.findById(id));
    }

    @GetMapping("/area/{area}")
    public ResponseEntity<List<Observator>> getByArea(@PathVariable String area) {
        return ResponseEntity.ok(observatorUseCase.findByAssignedArea(area));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Observator> update(@PathVariable Long id, @Valid @RequestBody Observator observator) {
        return ResponseEntity.ok(observatorUseCase.update(id, observator));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        observatorUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
