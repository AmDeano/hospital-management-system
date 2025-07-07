package com.hospital.patient.controller;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    
    // Create a new patient
    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientDto patientDto) {
        PatientDto createdPatient = patientService.createPatient(patientDto);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }
    
    // Get all patients
    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        List<PatientDto> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }
    
    // Get patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable String id) {
        PatientDto patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
    
    // Get patient by CIN
    @GetMapping("/cin/{cin}")
    public ResponseEntity<PatientDto> getPatientByCin(@PathVariable String cin) {
        PatientDto patient = patientService.getPatientByCin(cin);
        return ResponseEntity.ok(patient);
    }
    
    // Get patient by email
    @GetMapping("/email/{email}")
    public ResponseEntity<PatientDto> getPatientByEmail(@PathVariable String email) {
        PatientDto patient = patientService.getPatientByEmail(email);
        return ResponseEntity.ok(patient);
    }
    
    // Get minors by parent CIN
    @GetMapping("/minors/parent/{parentCin}")
    public ResponseEntity<List<PatientDto>> getMinorsByParentCin(@PathVariable String parentCin) {
        List<PatientDto> minors = patientService.getMinorsByParentCin(parentCin);
        return ResponseEntity.ok(minors);
    }
    
    // Get all minors
    @GetMapping("/minors")
    public ResponseEntity<List<PatientDto>> getAllMinors() {
        List<PatientDto> minors = patientService.getAllMinors();
        return ResponseEntity.ok(minors);
    }
    
    // Update patient
    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable String id, 
                                                   @Valid @RequestBody PatientDto patientDto) {
        PatientDto updatedPatient = patientService.updatePatient(id, patientDto);
        return ResponseEntity.ok(updatedPatient);
    }
    
    // Delete patient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    // Search patients by name
    @GetMapping("/search")
    public ResponseEntity<List<PatientDto>> searchPatientsByName(@RequestParam String nom) {
        List<PatientDto> patients = patientService.searchPatientsByName(nom);
        return ResponseEntity.ok(patients);
    }
    
    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Patient Service is running!");
    }
}