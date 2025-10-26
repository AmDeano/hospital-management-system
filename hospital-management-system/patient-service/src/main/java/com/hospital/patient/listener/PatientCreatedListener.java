package com.hospital.patient.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.hospital.common.events.PatientEvent;
import com.hospital.patient.config.RabbitConfig;
import com.hospital.patient.entity.Patient;
import com.hospital.patient.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PatientCreatedListener {

    private static final Logger logger = LoggerFactory.getLogger(PatientCreatedListener.class);
    private final PatientRepository patientRepository;

    public PatientCreatedListener(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
        logger.info("🎧 PatientCreatedListener initialized and ready to listen");
    }

    @RabbitListener(queues = RabbitConfig.PATIENT_QUEUE)
    public void handlePatientEvent(PatientEvent event) {
        try {
            logger.info("📩 Received PatientEvent: {}", event);

            if (event.getEventType() == null) {
                logger.warn("⚠️ Ignoring event with no type");
                return;
            }

            switch (event.getEventType()) {
                case "PATIENT_CREATED":
                    handlePatientCreated(event);
                    break;

                case "PATIENT_UPDATED":
                    handlePatientUpdated(event);
                    break;

                case "PATIENT_DELETED":
                    handlePatientDeleted(event);
                    break;

                default:
                    logger.warn("⚠️ Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            logger.error("❌ Error processing PatientEvent", e);
            throw e;
        }
    }

    private void handlePatientCreated(PatientEvent event) {
        try {
            logger.info("🔄 Processing PATIENT_CREATED for: {}", event.getPatientName());
            
            // Sanitize and validate CIN
            String cin = sanitize(event.getPatientCin());
            if (cin == null) {
                cin = event.getPatientId();
                logger.warn("⚠️ CIN is null, using patientId: {}", cin);
            }

            // Check if patient already exists
            Optional<Patient> existingPatient = patientRepository.findById(cin);
            if (existingPatient.isPresent()) {
                logger.warn("⚠️ Patient already exists with id: {}", cin);
                return;
            }

            // Create new patient
            Patient patient = new Patient();
            patient.setId(cin);
            
            // Set name
            String name = sanitize(event.getPatientName());
            patient.setNom(name != null ? name : "Unknown");
            
            // Set email
            patient.setEmail(event.getPatientEmail());
            
            // Set CIN
            patient.setCin(sanitize(event.getPatientCin()));
            
            // IMPORTANT: Parse and set dateNaissance
            String dateStr = sanitize(event.getDateNaissance());
            if (dateStr != null) {
                try {
                    LocalDate birthDate = LocalDate.parse(dateStr);
                    patient.setDateNaissance(birthDate);
                    logger.info("✅ Parsed dateNaissance: {}", birthDate);
                } catch (Exception e) {
                    logger.error("❌ Failed to parse dateNaissance: {}", dateStr, e);
                    patient.setDateNaissance(LocalDate.of(2000, 1, 1));
                }
            } else {
                logger.warn("⚠️ dateNaissance is null, using default");
                patient.setDateNaissance(LocalDate.of(2000, 1, 1));
            }
            
            // Set isMinor
            patient.setIsMinor(event.getIsMinor() != null ? event.getIsMinor() : false);
            
            // Set parentCin
            patient.setParentCin(sanitize(event.getParentCin()));
            
            // Set phone
            patient.setNumeroTelephone(sanitize(event.getNumeroTelephone()));
            
            // Set address
            patient.setAdresse(sanitize(event.getAdresse()));
            
            // Set NSS
            patient.setNumeroSecuriteSociale(sanitize(event.getNumeroSecuriteSociale()));
            
            // Set timestamps
            patient.setCreatedAt(LocalDateTime.now());
            patient.setUpdatedAt(LocalDateTime.now());

            // Save
            Patient savedPatient = patientRepository.save(patient);
            logger.info("✅ Patient created: {} (CIN: {}, Age: {})", 
                       savedPatient.getNom(), savedPatient.getId(), savedPatient.getAge());
                       
        } catch (Exception e) {
            logger.error("❌ Error in handlePatientCreated", e);
            throw e;
        }
    }

    private void handlePatientUpdated(PatientEvent event) {
        try {
            logger.info("🔄 Processing PATIENT_UPDATED");
            
            String cin = sanitize(event.getPatientCin());
            if (cin == null) {
                cin = event.getPatientId();
            }

            final String identifier = cin;

            patientRepository.findById(identifier).ifPresentOrElse(patient -> {
                // Update fields
                String name = sanitize(event.getPatientName());
                if (name != null) {
                    patient.setNom(name);
                }
                
                if (event.getPatientEmail() != null) {
                    patient.setEmail(event.getPatientEmail());
                }
                
                patient.setCin(sanitize(event.getPatientCin()));
                
                // Update dateNaissance
                String dateStr = sanitize(event.getDateNaissance());
                if (dateStr != null) {
                    try {
                        patient.setDateNaissance(LocalDate.parse(dateStr));
                    } catch (Exception e) {
                        logger.error("❌ Failed to parse dateNaissance: {}", dateStr, e);
                    }
                }
                
                if (event.getIsMinor() != null) {
                    patient.setIsMinor(event.getIsMinor());
                }
                
                patient.setParentCin(sanitize(event.getParentCin()));
                patient.setNumeroTelephone(sanitize(event.getNumeroTelephone()));
                patient.setAdresse(sanitize(event.getAdresse()));
                patient.setNumeroSecuriteSociale(sanitize(event.getNumeroSecuriteSociale()));
                patient.setUpdatedAt(LocalDateTime.now());
                
                patientRepository.save(patient);
                logger.info("🔁 Patient updated: {}", patient.getNom());
            }, () -> {
                logger.warn("⚠️ Patient not found for update, creating new one...");
                handlePatientCreated(event);
            });
            
        } catch (Exception e) {
            logger.error("❌ Error in handlePatientUpdated", e);
            throw e;
        }
    }

    private void handlePatientDeleted(PatientEvent event) {
        try {
            logger.info("🔄 Processing PATIENT_DELETED");
            
            String cin = sanitize(event.getPatientCin());
            if (cin == null) {
                cin = event.getPatientId();
            }

            final String identifier = cin;

            patientRepository.findById(identifier).ifPresent(patient -> {
                patientRepository.delete(patient);
                logger.info("🗑️ Patient deleted: {}", patient.getNom());
            });
            
        } catch (Exception e) {
            logger.error("❌ Error in handlePatientDeleted", e);
            throw e;
        }
    }

    /**
     * Sanitize string - converts "null" string and empty strings to actual null
     */
    private String sanitize(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return null;
        }
        return value.trim();
    }
}