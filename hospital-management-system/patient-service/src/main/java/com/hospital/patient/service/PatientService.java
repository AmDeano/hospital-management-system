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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientService {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private PatientEventPublisher eventPublisher;
    
    // Create a new patient
    public PatientDto createPatient(PatientDto patientDto) {
        logger.info("Creating new patient: {}", patientDto.getNom());
        
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
        PatientDto result = convertToDto(savedPatient);
        
        // Publish patient created event
        try {
            eventPublisher.publishPatientCreated(
                savedPatient.getId(),
                savedPatient.getNom(),
                savedPatient.getEmail(),
                savedPatient.getCin(),
                savedPatient.getIsMinor(),
                savedPatient.getParentCin()
            );
            logger.info("Patient created event published for ID: {}", savedPatient.getId());
        } catch (Exception e) {
            logger.error("Failed to publish patient created event for ID: {}", savedPatient.getId(), e);
            // Don't fail the operation if event publishing fails
        }
        
        return result;
    }
    
    // Get all patients
    @Transactional(readOnly = true)
    public List<PatientDto> getAllPatients() {
        logger.debug("Retrieving all patients");
        return patientRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get patient by ID
    @Transactional(readOnly = true)
    public PatientDto getPatientById(String id) {
        logger.debug("Retrieving patient by ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        return convertToDto(patient);
    }
    
    // Get patient by CIN
    @Transactional(readOnly = true)
    public PatientDto getPatientByCin(String cin) {
        logger.debug("Retrieving patient by CIN: {}", cin);
        Patient patient = patientRepository.findByCin(cin)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with CIN: " + cin));
        return convertToDto(patient);
    }
    
    // Get patient by email
    @Transactional(readOnly = true)
    public PatientDto getPatientByEmail(String email) {
        logger.debug("Retrieving patient by email: {}", email);
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with email: " + email));
        return convertToDto(patient);
    }
    
    // Get minors by parent CIN
    @Transactional(readOnly = true)
    public List<PatientDto> getMinorsByParentCin(String parentCin) {
        logger.debug("Retrieving minors for parent CIN: {}", parentCin);
        return patientRepository.findByParentCin(parentCin)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get all minors
    @Transactional(readOnly = true)
    public List<PatientDto> getAllMinors() {
        logger.debug("Retrieving all minors");
        return patientRepository.findByIsMinorTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Update patient
    public PatientDto updatePatient(String id, PatientDto patientDto) {
        logger.info("Updating patient with ID: {}", id);
        
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        
        // Store original values for event publishing
        String originalEmail = existingPatient.getEmail();
        String originalNom = existingPatient.getNom();
        String originalCin = existingPatient.getCin();
        Boolean originalIsMinor = existingPatient.getIsMinor();
        
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
        boolean ageStatusChanged = false;
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
                ageStatusChanged = true;
            }
            
            if (existingPatient.getIsMinor() != isMinor) {
                ageStatusChanged = true;
            }
            
            existingPatient.setIsMinor(isMinor);
        }
        
        Patient updatedPatient = patientRepository.save(existingPatient);
        PatientDto result = convertToDto(updatedPatient);
        
        // Publish patient updated event
        try {
            eventPublisher.publishPatientUpdated(
                updatedPatient.getId(),
                updatedPatient.getNom(),
                updatedPatient.getEmail(),
                updatedPatient.getCin(),
                updatedPatient.getIsMinor(),
                updatedPatient.getParentCin(),
                buildUpdateDetails(originalNom, originalEmail, originalCin, originalIsMinor, 
                                 updatedPatient, ageStatusChanged)
            );
            logger.info("Patient updated event published for ID: {}", updatedPatient.getId());
        } catch (Exception e) {
            logger.error("Failed to publish patient updated event for ID: {}", updatedPatient.getId(), e);
            // Don't fail the operation if event publishing fails
        }
        
        return result;
    }
    
    // Delete patient
    public void deletePatient(String id) {
        logger.info("Deleting patient with ID: {}", id);
        
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        
        // Store patient details for event before deletion
        String patientName = patient.getNom();
        String patientEmail = patient.getEmail();
        String patientCin = patient.getCin();
        Boolean isMinor = patient.getIsMinor();
        String parentCin = patient.getParentCin();
        
        patientRepository.deleteById(id);
        
        // Publish patient deleted event
        try {
            eventPublisher.publishPatientDeleted(id, patientName, patientEmail, patientCin, isMinor, parentCin);
            logger.info("Patient deleted event published for ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to publish patient deleted event for ID: {}", id, e);
            // Event publishing failure doesn't affect deletion
        }
    }
    
    // Search patients by name
    @Transactional(readOnly = true)
    public List<PatientDto> searchPatientsByName(String nom) {
        logger.debug("Searching patients by name: {}", nom);
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
    
    // Build update details for event
    private String buildUpdateDetails(String originalNom, String originalEmail, String originalCin, 
                                    Boolean originalIsMinor, Patient updatedPatient, boolean ageStatusChanged) {
        StringBuilder details = new StringBuilder();
        
        if (!originalNom.equals(updatedPatient.getNom())) {
            details.append("Name changed from '").append(originalNom).append("' to '").append(updatedPatient.getNom()).append("'; ");
        }
        
        if (!originalEmail.equals(updatedPatient.getEmail())) {
            details.append("Email changed from '").append(originalEmail).append("' to '").append(updatedPatient.getEmail()).append("'; ");
        }
        
        if (originalCin != null && !originalCin.equals(updatedPatient.getCin())) {
            details.append("CIN changed from '").append(originalCin).append("' to '").append(updatedPatient.getCin()).append("'; ");
        }
        
        if (ageStatusChanged) {
            details.append("Age status changed from ").append(originalIsMinor ? "minor" : "adult")
                   .append(" to ").append(updatedPatient.getIsMinor() ? "minor" : "adult").append("; ");
        }
        
        return details.length() > 0 ? details.toString() : "General update";
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