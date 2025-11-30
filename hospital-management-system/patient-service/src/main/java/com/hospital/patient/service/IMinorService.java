package com.hospital.patient.service;

import com.hospital.patient.dto.PatientDto;
import java.util.List;

/**
 * Interface for minor-specific patient operations.
 * Separates concerns and follows Interface Segregation Principle.
 */
public interface IMinorService {

    /**
     * Get all minor patients
     * @return list of all minors
     */
    List<PatientDto> getAllMinors();

    /**
     * Get all minors by parent CIN
     * @param parentCin the parent's CIN
     * @return list of minors with matching parent CIN
     */
    List<PatientDto> getMinorsByParentCin(String parentCin);
}
