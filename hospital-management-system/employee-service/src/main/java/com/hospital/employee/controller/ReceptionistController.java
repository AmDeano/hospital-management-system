package com.hospital.employee.controller;

import com.hospital.employee.entity.Receptionist;
import com.hospital.employee.usecase.ReceptionistUseCaseImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee-service/api/receptionists")
//@PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTION') or hasRole('SUPERVISOR')")
public class ReceptionistController {

    private final ReceptionistUseCaseImpl receptionistUseCase;

    public ReceptionistController(ReceptionistUseCaseImpl receptionistUseCase) {
        this.receptionistUseCase = receptionistUseCase;
    }

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Receptionist> create(@Valid @RequestBody Receptionist receptionist) {
        return ResponseEntity.status(HttpStatus.CREATED).body(receptionistUseCase.create(receptionist));
    }

    @GetMapping
    public ResponseEntity<List<Receptionist>> all() {
        return ResponseEntity.ok(receptionistUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Receptionist> get(@PathVariable Long id) {
        return ResponseEntity.ok(receptionistUseCase.findById(id));
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Receptionist> update(@PathVariable Long id, @Valid @RequestBody Receptionist receptionist) {
        return ResponseEntity.ok(receptionistUseCase.update(id, receptionist));
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        receptionistUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
