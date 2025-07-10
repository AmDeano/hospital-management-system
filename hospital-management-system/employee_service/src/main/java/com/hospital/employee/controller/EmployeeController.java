package com.hospital.employee.controller;

import com.hospital.employee.dto.EmployeeDto;
import com.hospital.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto dto) {
        return ResponseEntity.status(201).body(employeeService.createEmployee(dto));
    }

    @GetMapping
    public List<EmployeeDto> getAll() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{matricule}")
    public ResponseEntity<EmployeeDto> getByMatricule(@PathVariable String matricule) {
        return ResponseEntity.ok(employeeService.getEmployeeByMatricule(matricule));
    }

    @DeleteMapping("/{matricule}")
    public ResponseEntity<Void> delete(@PathVariable String matricule) {
        employeeService.deleteEmployee(matricule);
        return ResponseEntity.noContent().build();
    }
 // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Employee Service is running!");
    }

}
