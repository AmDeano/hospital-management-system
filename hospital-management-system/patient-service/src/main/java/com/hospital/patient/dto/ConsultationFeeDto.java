package com.hospital.patient.dto;

import com.hospital.patient.entity.ConsultationFee;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationFeeDto {
    private Long id;
    private Long doctorId;
    private String specialization;
    private BigDecimal feeAmount;
    private String currency;
    private Boolean isActive;

    public static ConsultationFeeDto fromEntity(ConsultationFee fee) {
        return ConsultationFeeDto.builder()
                .id(fee.getId())
                .doctorId(fee.getDoctorId())
                .specialization(fee.getSpecialization())
                .feeAmount(fee.getFeeAmount())
                .currency(fee.getCurrency())
                .isActive(fee.getIsActive())
                .build();
    }

    public ConsultationFee toEntity() {
        return ConsultationFee.builder()
                .doctorId(this.doctorId)
                .specialization(this.specialization)
                .feeAmount(this.feeAmount)
                .currency(this.currency != null ? this.currency : "USD")
                .isActive(this.isActive != null ? this.isActive : true)
                .build();
    }
}
