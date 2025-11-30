package com.hospital.patient.dto;

import com.hospital.patient.entity.PatientInsurance;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientInsuranceDto {
    private Long id;
    private String patientId;
    private String insuranceProvider;
    private String policyNumber;
    private Integer coveragePercentage;
    private Boolean isActive;
    private LocalDate expiryDate;

    public static PatientInsuranceDto fromEntity(PatientInsurance insurance) {
        return PatientInsuranceDto.builder()
                .id(insurance.getId())
                .patientId(insurance.getPatientId())
                .insuranceProvider(insurance.getInsuranceProvider())
                .policyNumber(insurance.getPolicyNumber())
                .coveragePercentage(insurance.getCoveragePercentage())
                .isActive(insurance.getIsActive())
                .expiryDate(insurance.getExpiryDate())
                .build();
    }

    public PatientInsurance toEntity() {
        return PatientInsurance.builder()
                .patientId(this.patientId)
                .insuranceProvider(this.insuranceProvider)
                .policyNumber(this.policyNumber)
                .coveragePercentage(this.coveragePercentage != null ? this.coveragePercentage : 80)
                .isActive(this.isActive != null ? this.isActive : true)
                .expiryDate(this.expiryDate)
                .build();
    }
}
