package com.hospital.employee.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.employee.entity.Department;
import com.hospital.employee.usecase.DepartmentUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentUseCase useCase;

    public DepartmentController(DepartmentUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<Department>> all() {
        return ResponseEntity.ok(useCase.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> create(@Valid @RequestBody Department d) {
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.save(d));
    }
}
