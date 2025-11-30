package com.hospital.patient.controller;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.service.IMinorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/minors")
@CrossOrigin(origins = "*")
public class MinorController {

    private final IMinorService minorService;

    /**
     * Constructor injection - follows dependency inversion principle
     */
    public MinorController(IMinorService minorService) {
        this.minorService = minorService;
    }

    /**
     * Get all minors
     */
    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllMinors() {
        List<PatientDto> minors = minorService.getAllMinors();
        return ResponseEntity.ok(minors);
    }

    /**
     * Get minors by parent CIN
     */
    @GetMapping("/parent/{parentCin}")
    public ResponseEntity<List<PatientDto>> getMinorsByParentCin(@PathVariable String parentCin) {
        List<PatientDto> minors = minorService.getMinorsByParentCin(parentCin);
        return ResponseEntity.ok(minors);
    }
}
