package com.hospital.employee.controller;

import com.hospital.employee.entity.Doctor;
import com.hospital.employee.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('SUPERVISOR')")
public class DoctorController {

    private final DoctorService doctorService;


    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }


    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.findById(id));
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(doctorService.findBySpecialization(specialization));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Doctor>> getAvailableDoctors() {
        return ResponseEntity.ok(doctorService.findAvailableDoctors());
    }

    @PostMapping("/{id}/patients/{patientId}/discharge")
    public ResponseEntity<String> authorizeDischarge(@PathVariable Long id, @PathVariable Long patientId) {
        return ResponseEntity.ok(doctorService.authorizePatientDischarge(id, patientId));
    }

    @PostMapping("/{id}/patients/{patientId}/certificate")
    public ResponseEntity<String> writeCertificate(@PathVariable Long id, @PathVariable Long patientId,
                                                   @RequestBody String details) {
        return ResponseEntity.ok(doctorService.writeMedicalCertificate(id, patientId, details));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @Valid @RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorService.update(id, doctor));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
