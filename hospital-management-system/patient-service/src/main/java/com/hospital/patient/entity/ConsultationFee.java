package com.hospital.patient.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultation_fees", indexes = {
    @Index(name = "idx_consultation_fee_doctor", columnList = "doctor_id"),
    @Index(name = "idx_consultation_fee_specialization", columnList = "specialization"),
    @Index(name = "idx_consultation_fee_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long doctorId;

    private String specialization;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal feeAmount;

    @Column(length = 3)
    private String currency = "USD";

    @Column(nullable = false)
    private Boolean isActive = true;

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
}
