package com.hospital.patient.service;

import com.hospital.common.events.PatientEvent;
import com.hospital.patient.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PatientEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PatientEventPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishPatientCreated(String patientId, String patientName, String patientEmail, 
                                    String patientCin, Boolean isMinor, String parentCin) {
        try {
            PatientEvent event = createBaseEvent("PATIENT_CREATED", patientId, patientName, patientEmail);
            event.setPatientCin(patientCin);
            event.setIsMinor(isMinor);
            event.setParentCin(parentCin);
            event.setEventData("New patient registered in the system");
            
            publishEvent(event);
            logger.info("✅ Published patient created event: {}", event);
        } catch (Exception e) {
            logger.error("❌ Error publishing patient created event", e);
            throw e;
        }
    }

    public void publishPatientUpdated(String patientId, String patientName, String patientEmail, 
                                    String patientCin, Boolean isMinor, String parentCin, String updateDetails) {
        try {
            PatientEvent event = createBaseEvent("PATIENT_UPDATED", patientId, patientName, patientEmail);
            event.setPatientCin(patientCin);
            event.setIsMinor(isMinor);
            event.setParentCin(parentCin);
            event.setEventData(updateDetails);
            
            publishEvent(event);
            logger.info("✅ Published patient updated event: {}", event);
        } catch (Exception e) {
            logger.error("❌ Error publishing patient updated event", e);
            throw e;
        }
    }

    public void publishPatientDeleted(String patientId, String patientName, String patientEmail, 
                                    String patientCin, Boolean isMinor, String parentCin) {
        try {
            PatientEvent event = createBaseEvent("PATIENT_DELETED", patientId, patientName, patientEmail);
            event.setPatientCin(patientCin);
            event.setIsMinor(isMinor);
            event.setParentCin(parentCin);
            event.setEventData("Patient removed from the system");
            
            publishEvent(event);
            logger.info("✅ Published patient deleted event: {}", event);
        } catch (Exception e) {
            logger.error("❌ Error publishing patient deleted event", e);
            throw e;
        }
    }

    public void publishPatientStatusChanged(String patientId, String patientName, String patientEmail, 
                                          String patientCin, Boolean wasMinor, Boolean isNowMinor) {
        try {
            PatientEvent event = createBaseEvent("PATIENT_STATUS_CHANGED", patientId, patientName, patientEmail);
            event.setPatientCin(patientCin);
            event.setIsMinor(isNowMinor);
            event.setEventData(String.format("Patient status changed from %s to %s", 
                                            wasMinor ? "minor" : "adult", 
                                            isNowMinor ? "minor" : "adult"));
            
            publishEvent(event);
            logger.info("✅ Published patient status changed event: {}", event);
        } catch (Exception e) {
            logger.error("❌ Error publishing patient status changed event", e);
            throw e;
        }
    }

    private PatientEvent createBaseEvent(String eventType, String patientId, String patientName, String patientEmail) {
        PatientEvent event = new PatientEvent();
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setPatientId(patientId);
        event.setPatientName(patientName);
        event.setPatientEmail(patientEmail);
        event.setTimestamp(LocalDateTime.now().toString()); // Changed to String
        return event;
    }

    private void publishEvent(PatientEvent event) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.EXCHANGE_NAME,
            RabbitConfig.PATIENT_ROUTING_KEY,
            event
        );
    }
}