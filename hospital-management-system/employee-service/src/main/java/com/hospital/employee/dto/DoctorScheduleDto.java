package com.hospital.employee.dto;

import com.hospital.employee.entity.Doctor;
import com.hospital.employee.entity.DoctorSchedule;
import lombok.*;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorScheduleDto {
    private Long id;
    private Long doctorId;
    private DoctorSchedule.DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAvailable;

    public static DoctorScheduleDto fromEntity(DoctorSchedule schedule) {
        return DoctorScheduleDto.builder()
                .id(schedule.getId())
                .doctorId(schedule.getDoctor().getId())
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .isAvailable(schedule.getIsAvailable())
                .build();
    }

    public DoctorSchedule toEntity(Doctor doctor) {
        return DoctorSchedule.builder()
                .dayOfWeek(this.dayOfWeek)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .isAvailable(this.isAvailable)
                .doctor(doctor)
                .build();
    }
}
