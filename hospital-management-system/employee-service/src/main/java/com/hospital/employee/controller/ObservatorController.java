package com.hospital.employee.controller;

import com.hospital.employee.entity.Observator;
import com.hospital.employee.service.ObservatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/observators")
@PreAuthorize("hasRole('ADMIN') or hasRole('OBSERVATOR')")
public class ObservatorController {

    private final ObservatorService observatorService;

    public ObservatorController(ObservatorService observatorService) {
        this.observatorService = observatorService;
    }

    @GetMapping
    public ResponseEntity<List<Observator>> all() {
        return ResponseEntity.ok(observatorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Observator> get(@PathVariable Long id) {
        return ResponseEntity.ok(observatorService.findById(id));
    }

    @GetMapping("/area/{area}")
    public ResponseEntity<List<Observator>> getByArea(@PathVariable String area) {
        return ResponseEntity.ok(observatorService.findByAssignedArea(area));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Observator> update(@PathVariable Long id, @Valid @RequestBody Observator observator) {
        return ResponseEntity.ok(observatorService.update(id, observator));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        observatorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
