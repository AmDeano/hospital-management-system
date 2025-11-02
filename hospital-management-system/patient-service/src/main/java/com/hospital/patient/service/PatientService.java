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

/**
 * Handles business logic for managing patients.
 * Also publishes synchronization events through RabbitMQ.
 */
@Service
@Transactional
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientEventPublisher patientEventPublisher; // ✅ fixed: using correct publisher

//    // -----------------------------------------------------------
//    // CREATE
//    // -----------------------------------------------------------
//    public PatientDto createPatient(PatientDto patientDto) {
//        logger.info("Creating new patient: {}", patientDto.getNom());
//
//        validatePatientData(patientDto);
//
//        // Check for duplicates
//        if (patientRepository.existsByEmail(patientDto.getEmail())) {
//            throw new DuplicatePatientException("Patient with email " + patientDto.getEmail() + " already exists");
//        }
//        if (patientRepository.existsByCin(patientDto.getCin())) {
//            throw new DuplicatePatientException("Patient with CIN " + patientDto.getCin() + " already exists");
//        }
//
//        // Convert DTO to entity
//        Patient patient = convertToEntity(patientDto);
//
//        // Generate ID based on CIN or minor status
//        generatePatientId(patient);
//
//        Patient savedPatient = patientRepository.save(patient);
//        PatientDto result = convertToDto(savedPatient);
//
//        // ✅ Publish event for synchronization
//        try {
//            patientEventPublisher.publishPatientCreated(
//                savedPatient.getId(),
//                savedPatient.getNom(),
//                savedPatient.getEmail(),
//                savedPatient.getCin(),
//                savedPatient.getIsMinor(),
//                savedPatient.getParentCin()
//            );
//            logger.info("✅ Patient created event published for ID: {}", savedPatient.getId());
//        } catch (Exception e) {
//            logger.error("⚠️ Failed to publish patient created event for ID: {}", savedPatient.getId(), e);
//        }
//
//        return result;
//    }

    // -----------------------------------------------------------
    // READ
    // -----------------------------------------------------------
    @Transactional(readOnly = true)
    public List<PatientDto> getAllPatients() {
        logger.debug("Retrieving all patients");
        return patientRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientDto getPatientById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        return convertToDto(patient);
    }

    @Transactional(readOnly = true)
    public PatientDto getPatientByCin(String cin) {
        Patient patient = patientRepository.findByCin(cin)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with CIN: " + cin));
        return convertToDto(patient);
    }

    @Transactional(readOnly = true)
    public PatientDto getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with email: " + email));
        return convertToDto(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientDto> getAllMinors() {
        logger.debug("Retrieving all minors");
        return patientRepository.findByIsMinorTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PatientDto> getMinorsByParentCin(String parentCin) {
        logger.debug("Retrieving minors for parent CIN: {}", parentCin);
        return patientRepository.findByParentCin(parentCin)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------
    public PatientDto updatePatient(String id, PatientDto patientDto) {
        logger.info("Updating patient with ID: {}", id);

        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        validatePatientData(patientDto);

        // Check for email conflict
        if (!existingPatient.getEmail().equals(patientDto.getEmail()) &&
                patientRepository.existsByEmail(patientDto.getEmail())) {
            throw new DuplicatePatientException("Email already used: " + patientDto.getEmail());
        }

        // Update fields
        existingPatient.setNom(patientDto.getNom());
        existingPatient.setDateNaissance(patientDto.getDateNaissance());
        existingPatient.setEmail(patientDto.getEmail());
        existingPatient.setNumeroTelephone(patientDto.getNumeroTelephone());
        existingPatient.setAdresse(patientDto.getAdresse());
        existingPatient.setCin(patientDto.getCin());
        existingPatient.setParentCin(patientDto.getParentCin());

        // Update age/minor status
        int age = Period.between(patientDto.getDateNaissance(), LocalDate.now()).getYears();
        existingPatient.setIsMinor(age < 18);

        Patient updatedPatient = patientRepository.save(existingPatient);
        PatientDto result = convertToDto(updatedPatient);

        // ✅ Publish update event
        try {
            patientEventPublisher.publishPatientUpdated(
                updatedPatient.getId(),
                updatedPatient.getNom(),
                updatedPatient.getEmail(),
                updatedPatient.getCin(),
                updatedPatient.getIsMinor(),
                updatedPatient.getParentCin(),
                "Patient updated successfully"
            );
            logger.info("🔁 Patient updated event published for ID: {}", updatedPatient.getId());
        } catch (Exception e) {
            logger.error("⚠️ Failed to publish patient updated event for ID: {}", updatedPatient.getId(), e);
        }

        return result;
    }

    // -----------------------------------------------------------
    // DELETE
    // -----------------------------------------------------------
    public void deletePatient(String id) {
        logger.info("Deleting patient with ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        patientRepository.deleteById(id);

        // ✅ Publish deletion event
        try {
            patientEventPublisher.publishPatientDeleted(
                id,
                patient.getNom(),
                patient.getEmail(),
                patient.getCin(),
                patient.getIsMinor(),
                patient.getParentCin()
            );
            logger.info("🗑️ Patient deleted event published for ID: {}", id);
        } catch (Exception e) {
            logger.error("⚠️ Failed to publish patient deleted event for ID: {}", id, e);
        }
    }

    // -----------------------------------------------------------
    // UTILITIES
    // -----------------------------------------------------------
    private void validatePatientData(PatientDto patientDto) {
        if (patientDto.getDateNaissance() == null) {
            throw new InvalidPatientDataException("Date de naissance requise");
        }

        int age = Period.between(patientDto.getDateNaissance(), LocalDate.now()).getYears();
        boolean isMinor = age < 18;
        patientDto.setIsMinor(isMinor);

        if (!isMinor && (patientDto.getCin() == null || patientDto.getCin().trim().isEmpty())) {
            throw new InvalidPatientDataException("CIN requis pour les patients adultes");
        }

        if (isMinor && (patientDto.getParentCin() == null || patientDto.getParentCin().trim().isEmpty())) {
            throw new InvalidPatientDataException("Parent CIN requis pour les mineurs");
        }
    }

    private void generatePatientId(Patient patient) {
        if (patient.getIsMinor() != null && patient.getIsMinor()) {
            Integer nextId = patientRepository.getNextMinorId();
            patient.setId(String.format("MIN-%04d", nextId));
        } else {
            patient.setId(patient.getCin());
        }
    }

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

    private Patient convertToEntity(PatientDto dto) {
        return new Patient(
                dto.getNom(),
                dto.getDateNaissance(),
                dto.getEmail(),
                dto.getNumeroTelephone(),
                dto.getAdresse(),
                dto.getNumeroSecuriteSociale(),
                dto.getCin(),
                dto.getParentCin()
        );
    }
    /**
     * Search patients by name (case-insensitive, partial match)
     * @param nom partial or full name to search
     * @return list of matching PatientDto
     */
    @Transactional(readOnly = true)
    public List<PatientDto> searchPatientsByName(String nom) {
        List<Patient> patients = patientRepository.findByNomContainingIgnoreCase(nom);

        if (patients.isEmpty()) {
            throw new PatientNotFoundException("No patients found with name containing: " + nom);
        }

        return patients.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
