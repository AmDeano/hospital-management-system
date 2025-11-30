package com.hospital.patient.dto;

import com.hospital.patient.entity.Prescription;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDto {
    private Long id;
    private String patientId;
    private Long doctorId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public static PrescriptionDto fromEntity(Prescription prescription) {
        return PrescriptionDto.builder()
                .id(prescription.getId())
                .patientId(prescription.getPatientId())
                .doctorId(prescription.getDoctorId())
                .medicationName(prescription.getMedicationName())
                .dosage(prescription.getDosage())
                .frequency(prescription.getFrequency())
                .durationDays(prescription.getDurationDays())
                .notes(prescription.getNotes())
                .isActive(prescription.getIsActive())
                .createdAt(prescription.getCreatedAt())
                .build();
    }

    public Prescription toEntity() {
        return Prescription.builder()
                .patientId(this.patientId)
                .doctorId(this.doctorId)
                .medicationName(this.medicationName)
                .dosage(this.dosage)
                .frequency(this.frequency)
                .durationDays(this.durationDays)
                .notes(this.notes)
                .isActive(this.isActive != null ? this.isActive : true)
                .build();
    }
}
