package com.hospital.patient.service;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.entity.Patient;
import com.hospital.patient.exception.PatientNotFoundException;
import com.hospital.patient.exception.DuplicatePatientException;
import com.hospital.patient.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    // Create a new patient
    public PatientDto createPatient(PatientDto patientDto) {
        // Check if email already exists
        if (patientRepository.existsByEmail(patientDto.getEmail())) {
            throw new DuplicatePatientException("Patient with email " + patientDto.getEmail() + " already exists");
        }
        
        // Check if social security number already exists
        if (patientRepository.existsByNumeroSecuriteSociale(patientDto.getNumeroSecuriteSociale())) {
            throw new DuplicatePatientException("Patient with SSN " + patientDto.getNumeroSecuriteSociale() + " already exists");
        }
        
        Patient patient = convertToEntity(patientDto);
        Patient savedPatient = patientRepository.save(patient);
        return convertToDto(savedPatient);
    }
    
    // Get all patients
    @Transactional(readOnly = true)
    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get patient by ID
    @Transactional(readOnly = true)
    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        return convertToDto(patient);
    }
    
    // Get patient by email
    @Transactional(readOnly = true)
    public PatientDto getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with email: " + email));
        return convertToDto(patient);
    }
    
    // Update patient
    public PatientDto updatePatient(Long id, PatientDto patientDto) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        
        // Check if email is being updated and if it already exists
        if (!existingPatient.getEmail().equals(patientDto.getEmail()) && 
            patientRepository.existsByEmail(patientDto.getEmail())) {
            throw new DuplicatePatientException("Patient with email " + patientDto.getEmail() + " already exists");
        }
        
        // Update fields
        existingPatient.setNom(patientDto.getNom());
        existingPatient.setDateNaissance(patientDto.getDateNaissance());
        existingPatient.setEmail(patientDto.getEmail());
        existingPatient.setNumeroTelephone(patientDto.getNumeroTelephone());
        existingPatient.setAdresse(patientDto.getAdresse());
        existingPatient.setNumeroSecuriteSociale(patientDto.getNumeroSecuriteSociale());
        
        Patient updatedPatient = patientRepository.save(existingPatient);
        return convertToDto(updatedPatient);
    }
    
    // Delete patient
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
    
    // Search patients by name
    @Transactional(readOnly = true)
    public List<PatientDto> searchPatientsByName(String nom) {
        return patientRepository.findByNomContainingIgnoreCase(nom)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Convert Entity to DTO
    private PatientDto convertToDto(Patient patient) {
        return new PatientDto(
                patient.getId(),
                patient.getNom(),
                patient.getDateNaissance(),
                patient.getEmail(),
                patient.getNumeroTelephone(),
                patient.getAdresse(),
                patient.getNumeroSecuriteSociale(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }
    
    // Convert DTO to Entity
    private Patient convertToEntity(PatientDto patientDto) {
        return new Patient(
                patientDto.getNom(),
                patientDto.getDateNaissance(),
                patientDto.getEmail(),
                patientDto.getNumeroTelephone(),
                patientDto.getAdresse(),
                patientDto.getNumeroSecuriteSociale()
        );
    }
}