package com.hospital.patient.service;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.entity.Patient;
import com.hospital.patient.exception.PatientNotFoundException;
import com.hospital.patient.exception.DuplicatePatientException;
import com.hospital.patient.exception.InvalidPatientDataException;
import com.hospital.patient.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    // Create a new patient
    public PatientDto createPatient(PatientDto patientDto) {
        // Validate patient data
        validatePatientData(patientDto);
        
        // Check if email already exists
        if (patientDto.getEmail() != null && patientRepository.existsByEmail(patientDto.getEmail())) {
            throw new DuplicatePatientException("Patient with email " + patientDto.getEmail() + " already exists");
        }
        
        // Check if social security number already exists
        if (patientDto.getNumeroSecuriteSociale() != null && 
            patientRepository.existsByNumeroSecuriteSociale(patientDto.getNumeroSecuriteSociale())) {
            throw new DuplicatePatientException("Patient with SSN " + patientDto.getNumeroSecuriteSociale() + " already exists");
        }
        
        Patient patient = convertToEntity(patientDto);
        
        // Generate ID based on age
        generatePatientId(patient);
        
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
    public PatientDto getPatientById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        return convertToDto(patient);
    }
    
    // Get patient by CIN
    @Transactional(readOnly = true)
    public PatientDto getPatientByCin(String cin) {
        Patient patient = patientRepository.findByCin(cin)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with CIN: " + cin));
        return convertToDto(patient);
    }
    
    // Get patient by email
    @Transactional(readOnly = true)
    public PatientDto getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with email: " + email));
        return convertToDto(patient);
    }
    
    // Get minors by parent CIN
    @Transactional(readOnly = true)
    public List<PatientDto> getMinorsByParentCin(String parentCin) {
        return patientRepository.findByParentCin(parentCin)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get all minors
    @Transactional(readOnly = true)
    public List<PatientDto> getAllMinors() {
        return patientRepository.findByIsMinorTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Update patient
    public PatientDto updatePatient(String id, PatientDto patientDto) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        
        // Validate updated data
        validatePatientData(patientDto);
        
        // Check if email is being updated and if it already exists
        if (patientDto.getEmail() != null && 
            !existingPatient.getEmail().equals(patientDto.getEmail()) && 
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
        existingPatient.setCin(patientDto.getCin());
        existingPatient.setParentCin(patientDto.getParentCin());
        
        // Check if age status changed (minor to adult or vice versa)
        if (patientDto.getDateNaissance() != null) {
            int age = Period.between(patientDto.getDateNaissance(), LocalDate.now()).getYears();
            boolean isMinor = age < 18;
            
            // If status changed from minor to adult, need to update ID
            if (existingPatient.getIsMinor() && !isMinor) {
                // Adult now needs CIN
                if (patientDto.getCin() == null || patientDto.getCin().trim().isEmpty()) {
                    throw new InvalidPatientDataException("CIN is required for adult patients");
                }
                existingPatient.setId(patientDto.getCin());
            }
            
            existingPatient.setIsMinor(isMinor);
        }
        
        Patient updatedPatient = patientRepository.save(existingPatient);
        return convertToDto(updatedPatient);
    }
    
    // Delete patient
    public void deletePatient(String id) {
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
    
    // Validate patient data
    private void validatePatientData(PatientDto patientDto) {
        if (patientDto.getDateNaissance() == null) {
            throw new InvalidPatientDataException("Birth date is required");
        }
        
        int age = Period.between(patientDto.getDateNaissance(), LocalDate.now()).getYears();
        boolean isMinor = age < 18;
        
        if (!isMinor) {
            // Adult patient must have CIN
            if (patientDto.getCin() == null || patientDto.getCin().trim().isEmpty()) {
                throw new InvalidPatientDataException("CIN is required for adult patients");
            }
            
            // Check if CIN already exists
            if (patientRepository.existsByCin(patientDto.getCin())) {
                throw new DuplicatePatientException("Patient with CIN " + patientDto.getCin() + " already exists");
            }
        } else {
            // Minor patient must have parent CIN
            if (patientDto.getParentCin() == null || patientDto.getParentCin().trim().isEmpty()) {
                throw new InvalidPatientDataException("Parent CIN is required for minor patients");
            }
            
            // Verify parent exists
            if (!patientRepository.existsByCin(patientDto.getParentCin())) {
                throw new InvalidPatientDataException("Parent with CIN " + patientDto.getParentCin() + " not found");
            }
        }
    }
    
    // Generate patient ID
    private void generatePatientId(Patient patient) {
        if (patient.getIsMinor()) {
            // Generate auto ID for minor: MIN-XXXX
            Integer nextId = patientRepository.getNextMinorId();
            patient.setId(String.format("MIN-%04d", nextId));
        } else {
            // Use CIN as ID for adults
            patient.setId(patient.getCin());
        }
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
                patient.getCin(),
                patient.getIsMinor(),
                patient.getParentCin(),
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
                patientDto.getNumeroSecuriteSociale(),
                patientDto.getCin(),
                patientDto.getParentCin()
        );
    }
}