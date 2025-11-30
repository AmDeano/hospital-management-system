package com.hospital.patient.dto;

import com.hospital.patient.entity.MedicalRecord;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordDto {
    private Long id;
    private String patientId;
    private Long doctorId;
    private String diagnosis;
    private String treatmentPlan;
    private String notes;
    private MedicalRecord.RecordType recordType;
    private LocalDateTime visitDate;
    private LocalDateTime createdAt;

    public static MedicalRecordDto fromEntity(MedicalRecord record) {
        return MedicalRecordDto.builder()
                .id(record.getId())
                .patientId(record.getPatientId())
                .doctorId(record.getDoctorId())
                .diagnosis(record.getDiagnosis())
                .treatmentPlan(record.getTreatmentPlan())
                .notes(record.getNotes())
                .recordType(record.getRecordType())
                .visitDate(record.getVisitDate())
                .createdAt(record.getCreatedAt())
                .build();
    }

    public MedicalRecord toEntity() {
        return MedicalRecord.builder()
                .patientId(this.patientId)
                .doctorId(this.doctorId)
                .diagnosis(this.diagnosis)
                .treatmentPlan(this.treatmentPlan)
                .notes(this.notes)
                .recordType(this.recordType != null ? this.recordType : MedicalRecord.RecordType.CONSULTATION)
                .visitDate(this.visitDate)
                .build();
    }
}
