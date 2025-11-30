package com.hospital.patient.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_insurance", uniqueConstraints = {
    @UniqueConstraint(columnNames = "patient_id")
}, indexes = {
    @Index(name = "idx_insurance_patient", columnList = "patient_id"),
    @Index(name = "idx_insurance_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientInsurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String patientId;

    private String insuranceProvider;

    private String policyNumber;

    @Column(nullable = false)
    private Integer coveragePercentage = 80;

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDate expiryDate;

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

    public boolean isValid() {
        return isActive && (expiryDate == null || expiryDate.isAfter(LocalDate.now()));
    }
}
