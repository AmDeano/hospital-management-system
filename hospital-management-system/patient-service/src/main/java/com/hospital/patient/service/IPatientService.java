package com.hospital.patient.service;

import com.hospital.patient.dto.PatientDto;
import java.util.List;

/**
 * Interface defining patient service operations.
 * Implements the Dependency Inversion Principle.
 */
public interface IPatientService {

    // READ operations
    List<PatientDto> getAllPatients();
    PatientDto getPatientById(String id);
    PatientDto getPatientByCin(String cin);
    PatientDto getPatientByEmail(String email);
    List<PatientDto> getAllMinors();
    List<PatientDto> getMinorsByParentCin(String parentCin);
    List<PatientDto> searchPatientsByName(String nom);

    // UPDATE operation
    PatientDto updatePatient(String id, PatientDto patientDto);

    // DELETE operation
    void deletePatient(String id);
}
