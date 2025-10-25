package com.hospital.auth.service;

import com.hospital.common.events.PatientEvent;
import com.hospital.auth.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PatientEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PatientEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public PatientEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPatientCreatedEvent(
            String patientId,
            String patientName,
            String patientEmail,
            String patientCin,
            LocalDate dateNaissance,
            Boolean isMinor,
            String parentCin,
            String numeroTelephone,
            String adresse,
            String numeroSecuriteSociale
    ) {
        try {
            // Use builder pattern for cleaner code
            PatientEvent event = PatientEvent.builder()
                    .eventType("PATIENT_CREATED")
                    .patientId(patientId)
                    .patientName(sanitize(patientName))
                    .patientEmail(patientEmail)
                    .patientCin(sanitize(patientCin))
                    .dateNaissance(dateNaissance != null ? dateNaissance.toString() : null)
                    .isMinor(isMinor != null ? isMinor : false)
                    .parentCin(sanitize(parentCin))
                    .numeroTelephone(sanitize(numeroTelephone))
                    .adresse(sanitize(adresse))
                    .numeroSecuriteSociale(sanitize(numeroSecuriteSociale))
                    .eventData("New patient registered in the auth-service")
                    .build();

            logger.info("📤 Publishing PatientCreatedEvent for CIN: {} (Name: {})", 
                       event.getPatientCin(), event.getPatientName());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.PATIENT_EXCHANGE,
                    RabbitConfig.PATIENT_ROUTING_KEY,
                    event
            );

            logger.info("✅ Successfully published PatientCreatedEvent");
            
        } catch (Exception e) {
            logger.error("❌ Error publishing PatientCreatedEvent for patient: {}", patientName, e);
            throw new RuntimeException("Failed to publish patient created event", e);
        }
    }

    public void publishPatientUpdatedEvent(
            String patientId,
            String patientName,
            String patientEmail,
            String patientCin,
            LocalDate dateNaissance,
            Boolean isMinor,
            String parentCin,
            String numeroTelephone,
            String adresse,
            String numeroSecuriteSociale
    ) {
        try {
            PatientEvent event = PatientEvent.builder()
                    .eventType("PATIENT_UPDATED")
                    .patientId(patientId)
                    .patientName(sanitize(patientName))
                    .patientEmail(patientEmail)
                    .patientCin(sanitize(patientCin))
                    .dateNaissance(dateNaissance != null ? dateNaissance.toString() : null)
                    .isMinor(isMinor != null ? isMinor : false)
                    .parentCin(sanitize(parentCin))
                    .numeroTelephone(sanitize(numeroTelephone))
                    .adresse(sanitize(adresse))
                    .numeroSecuriteSociale(sanitize(numeroSecuriteSociale))
                    .eventData("Patient information updated")
                    .build();

            logger.info("📤 Publishing PatientUpdatedEvent for CIN: {}", event.getPatientCin());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.PATIENT_EXCHANGE,
                    RabbitConfig.PATIENT_ROUTING_KEY,
                    event
            );

            logger.info("✅ Successfully published PatientUpdatedEvent");
            
        } catch (Exception e) {
            logger.error("❌ Error publishing PatientUpdatedEvent for patient: {}", patientName, e);
            throw new RuntimeException("Failed to publish patient updated event", e);
        }
    }

    public void publishPatientDeletedEvent(String patientId, String patientCin) {
        try {
            PatientEvent event = PatientEvent.builder()
                    .eventType("PATIENT_DELETED")
                    .patientId(patientId)
                    .patientCin(sanitize(patientCin))
                    .eventData("Patient account deleted")
                    .build();

            logger.info("📤 Publishing PatientDeletedEvent for CIN: {}", event.getPatientCin());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.PATIENT_EXCHANGE,
                    RabbitConfig.PATIENT_ROUTING_KEY,
                    event
            );

            logger.info("✅ Successfully published PatientDeletedEvent");
            
        } catch (Exception e) {
            logger.error("❌ Error publishing PatientDeletedEvent for CIN: {}", patientCin, e);
            throw new RuntimeException("Failed to publish patient deleted event", e);
        }
    }

    /**
     * Sanitize string values to avoid sending "null" strings
     */
    private String sanitize(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return null;
        }
        return value.trim();
    }
}