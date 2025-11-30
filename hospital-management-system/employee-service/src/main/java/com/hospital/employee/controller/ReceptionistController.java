package com.hospital.employee.controller;

import com.hospital.employee.entity.Receptionist;
import com.hospital.employee.service.ReceptionistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/receptionists")
@PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST') or hasRole('SUPERVISOR')")
public class ReceptionistController {

    private final ReceptionistService receptionistService;

    public ReceptionistController(ReceptionistService receptionistService) {
        this.receptionistService = receptionistService;
    }

    @GetMapping
    public ResponseEntity<List<Receptionist>> all() {
        return ResponseEntity.ok(receptionistService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Receptionist> get(@PathVariable Long id) {
        return ResponseEntity.ok(receptionistService.findById(id));
    }

    @GetMapping("/desk/{deskNumber}")
    public ResponseEntity<Optional<Receptionist>> getByDeskNumber(@PathVariable String deskNumber) {
        return ResponseEntity.ok(receptionistService.findByDeskNumber(deskNumber));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Receptionist> update(@PathVariable Long id, @Valid @RequestBody Receptionist receptionist) {
        return ResponseEntity.ok(receptionistService.update(id, receptionist));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        receptionistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
