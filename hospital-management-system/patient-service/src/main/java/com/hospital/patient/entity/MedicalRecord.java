package com.hospital.patient.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records", indexes = {
    @Index(name = "idx_medical_record_patient", columnList = "patient_id"),
    @Index(name = "idx_medical_record_doctor", columnList = "doctor_id"),
    @Index(name = "idx_medical_record_visit_date", columnList = "visit_date"),
    @Index(name = "idx_medical_record_type", columnList = "record_type"),
    @Index(name = "idx_medical_record_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientId;

    @Column(nullable = false)
    private Long doctorId;

    @Column(nullable = false, length = 500)
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType recordType = RecordType.CONSULTATION;

    @Column(nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum RecordType {
        CONSULTATION, DIAGNOSIS, TREATMENT, FOLLOW_UP
    }
}
