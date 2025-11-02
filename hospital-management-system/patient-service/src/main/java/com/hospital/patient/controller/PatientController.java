package com.hospital.patient.controller;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.service.PatientService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {
    
	@Autowired
	private PatientService patientService;

    
    
    // ✅ Get all patients or search by name
    @GetMapping
    public ResponseEntity<List<PatientDto>> getPatients(@RequestParam(name = "name", required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(patientService.searchPatientsByName(name));
        }
        return ResponseEntity.ok(patientService.getAllPatients());
    }
    
 // Get patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable("id") String id) {
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
    
    // Update patient
    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable("id") String id, 
                                                   @Valid @RequestBody PatientDto patientDto) {
        PatientDto updatedPatient = patientService.updatePatient(id, patientDto);
        return ResponseEntity.ok(updatedPatient);
    }
    
    // Delete patient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable("id") String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}