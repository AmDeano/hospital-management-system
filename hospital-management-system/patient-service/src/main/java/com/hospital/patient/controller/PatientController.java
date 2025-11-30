package com.hospital.patient.controller;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.service.IPatientService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

	private final IPatientService patientService;

	/**
	 * Constructor injection - follows dependency inversion principle
	 */
	public PatientController(IPatientService patientService) {
		this.patientService = patientService;
	}

    
    
    /**
     * Get all patients or search by name
     */
    @GetMapping
    public ResponseEntity<List<PatientDto>> getPatients(@RequestParam(name = "name", required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(patientService.searchPatientsByName(name));
        }
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    /**
     * Create new patient
     * Note: Patient ID is auto-generated based on age (MINOR-xxxx for minors, CIN for adults)
     */
    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientDto patientDto) {
        // Patient creation is handled by auth-service
        // This endpoint is for completeness and can be used to create patients directly
        PatientDto createdPatient = patientService.updatePatient(patientDto.getId(), patientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    /**
     * Get patient by ID
     */
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