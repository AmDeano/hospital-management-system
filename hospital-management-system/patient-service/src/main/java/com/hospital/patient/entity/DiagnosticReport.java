package com.hospital.patient.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostic_reports", indexes = {
    @Index(name = "idx_diagnostic_patient", columnList = "patient_id"),
    @Index(name = "idx_diagnostic_doctor", columnList = "doctor_id"),
    @Index(name = "idx_diagnostic_status", columnList = "status"),
    @Index(name = "idx_diagnostic_report_date", columnList = "report_date"),
    @Index(name = "idx_diagnostic_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientId;

    @Column(nullable = false)
    private Long doctorId;

    @Column(nullable = false)
    private Long testId;

    @Column(nullable = false)
    private String resultValue;

    @Column(nullable = false)
    private LocalDateTime reportDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 500)
    private String attachmentUrl;

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

    public enum ReportStatus {
        PENDING, COMPLETED, ABNORMAL
    }
}
