package com.hospital.auth.service;

import com.hospital.common.events.PatientEvent;
import com.hospital.auth.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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
            Boolean isMinor,
            String parentCin
    ) {
        try {
            PatientEvent event = new PatientEvent();
            event.setEventId(UUID.randomUUID().toString());
            event.setEventType("PATIENT_CREATED");
            event.setPatientId(patientId);
            event.setPatientName(patientName);
            event.setPatientEmail(patientEmail);
            event.setPatientCin(patientCin);
            event.setIsMinor(isMinor);
            event.setParentCin(parentCin);
            event.setEventData("New patient registered in the auth-service");
            event.setTimestamp(LocalDateTime.now());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.PATIENT_EXCHANGE,
                    RabbitConfig.PATIENT_ROUTING_KEY,
                    event
            );

            logger.info("✅ Published PatientCreatedEvent: {}", event);
        } catch (Exception e) {
            logger.error("❌ Error while publishing PatientCreatedEvent", e);
            throw e;
        }
    }
}
