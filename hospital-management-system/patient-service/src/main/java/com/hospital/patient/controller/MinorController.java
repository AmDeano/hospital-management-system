package com.hospital.patient.controller;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/minors")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MinorController {

    private final PatientService patientService = new PatientService();

    // Get all minors
    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllMinors() {
        List<PatientDto> minors = patientService.getAllMinors();
        return ResponseEntity.ok(minors);
    }

    // Get minors by parent CIN
    @GetMapping("/parent/{parentCin}")
    public ResponseEntity<List<PatientDto>> getMinorsByParentCin(@PathVariable String parentCin) {
        List<PatientDto> minors = patientService.getMinorsByParentCin(parentCin);
        return ResponseEntity.ok(minors);
    }
}
