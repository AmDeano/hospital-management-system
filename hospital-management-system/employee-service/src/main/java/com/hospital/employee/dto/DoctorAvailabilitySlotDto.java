package com.hospital.employee.dto;

import com.hospital.employee.entity.Doctor;
import com.hospital.employee.entity.DoctorAvailabilitySlot;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAvailabilitySlotDto {
    private Long id;
    private Long doctorId;
    private LocalDate availableDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAvailable;
    private String notes;

    public static DoctorAvailabilitySlotDto fromEntity(DoctorAvailabilitySlot slot) {
        return DoctorAvailabilitySlotDto.builder()
                .id(slot.getId())
                .doctorId(slot.getDoctor().getId())
                .availableDate(slot.getAvailableDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .isAvailable(slot.getIsAvailable())
                .notes(slot.getNotes())
                .build();
    }

    public DoctorAvailabilitySlot toEntity(Doctor doctor) {
        return DoctorAvailabilitySlot.builder()
                .availableDate(this.availableDate)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .isAvailable(this.isAvailable)
                .notes(this.notes)
                .doctor(doctor)
                .build();
    }
}
