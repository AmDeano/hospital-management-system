package com.hospital.patient.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.hospital.common.events.PatientEvent;
import com.hospital.patient.config.RabbitConfig;
import com.hospital.patient.entity.Patient;
import com.hospital.patient.repository.PatientRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PatientCreatedListener {

    private final PatientRepository patientRepository;

    public PatientCreatedListener(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Listen to PatientEvent messages from RabbitMQ and synchronize patient data into patientdb.
     */
    @RabbitListener(queues = RabbitConfig.PATIENT_QUEUE)
    public void handlePatientEvent(PatientEvent event) {
        System.out.println("📩 Received PatientEvent: " + event);

        if (event.getEventType() == null) {
            System.out.println("⚠️ Ignoring event with no type");
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
                System.out.println("⚠️ Unknown event type: " + event.getEventType());
        }
    }

    private void handlePatientCreated(PatientEvent event) {
        // Use CIN if available, otherwise use patientId
        String identifier = (event.getPatientCin() != null && !event.getPatientCin().isEmpty())
                ? event.getPatientCin()
                : event.getPatientId();

        Optional<Patient> existingPatient = patientRepository.findById(identifier);
        if (existingPatient.isPresent()) {
            System.out.println("⚠️ Patient already exists with id/cin: " + identifier);
            return;
        }

        Patient patient = new Patient();
        patient.setId(identifier);
        patient.setNom(event.getPatientName());
        patient.setEmail(event.getPatientEmail());
        patient.setCin(event.getPatientCin());
        patient.setIsMinor(event.getIsMinor());
        patient.setParentCin(event.getParentCin());
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());

        patientRepository.save(patient);
        System.out.println("✅ Patient created: " + patient.getNom());
    }

    private void handlePatientUpdated(PatientEvent event) {
        String identifier = (event.getPatientCin() != null && !event.getPatientCin().isEmpty())
                ? event.getPatientCin()
                : event.getPatientId();

        patientRepository.findById(identifier).ifPresentOrElse(patient -> {
            patient.setNom(event.getPatientName());
            patient.setEmail(event.getPatientEmail());
            patient.setCin(event.getPatientCin());
            patient.setIsMinor(event.getIsMinor());
            patient.setParentCin(event.getParentCin());
            patient.setUpdatedAt(LocalDateTime.now());
            patientRepository.save(patient);
            System.out.println("🔁 Patient updated: " + patient.getNom());
        }, () -> {
            System.out.println("⚠️ Patient not found for update, creating new one...");
            handlePatientCreated(event);
        });
    }

    private void handlePatientDeleted(PatientEvent event) {
        String identifier = (event.getPatientCin() != null && !event.getPatientCin().isEmpty())
                ? event.getPatientCin()
                : event.getPatientId();

        patientRepository.findById(identifier).ifPresent(patient -> {
            patientRepository.delete(patient);
            System.out.println("🗑️ Patient deleted: " + patient.getNom());
        });
    }
}
