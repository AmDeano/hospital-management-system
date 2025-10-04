package com.hospital.employee.controller;

import com.hospital.employee.entity.Doctor;
import com.hospital.employee.usecase.DoctorUseCaseImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('SUPERVISOR')")
public class DoctorController {

    private final DoctorUseCaseImpl doctorUseCase;


    public DoctorController(DoctorUseCaseImpl doctorUseCase) {
        this.doctorUseCase = doctorUseCase;
    }


    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorUseCase.findById(id));
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(doctorUseCase.findBySpecialization(specialization));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Doctor>> getAvailableDoctors() {
        return ResponseEntity.ok(doctorUseCase.findAvailableDoctors());
    }

    @PostMapping("/{id}/patients/{patientId}/discharge")
    public ResponseEntity<String> authorizeDischarge(@PathVariable Long id, @PathVariable Long patientId) {
        return ResponseEntity.ok(doctorUseCase.authorizePatientDischarge(id, patientId));
    }

    @PostMapping("/{id}/patients/{patientId}/certificate")
    public ResponseEntity<String> writeCertificate(@PathVariable Long id, @PathVariable Long patientId,
                                                   @RequestBody String details) {
        return ResponseEntity.ok(doctorUseCase.writeMedicalCertificate(id, patientId, details));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @Valid @RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorUseCase.update(id, doctor));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
