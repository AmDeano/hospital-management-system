package com.hospital.employee.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDashboardDto {
    private Long id;
    private String matricule;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String specialization;
    private String licenseNumber;
    private String medicalDegree;
    private String departmentName;
    private Boolean isActive;

    // Dashboard stats
    private Integer totalPatientsCount;
    private Integer appointmentsTodayCount;
    private Integer pendingPrescriptionsCount;
    private Integer recentMedicalRecordsCount;

    // Doctor's schedules and availability
    private List<DoctorScheduleDto> schedules;
    private List<DoctorAvailabilitySlotDto> availabilitySlots;

    public static DoctorDashboardDto fromDoctor(DoctorDto doctorDto) {
        DoctorDashboardDto dashboard = new DoctorDashboardDto();
        dashboard.setMatricule(doctorDto.getMatricule());
        dashboard.setFirstName(doctorDto.getFirstName());
        dashboard.setLastName(doctorDto.getLastName());
        dashboard.setEmail(doctorDto.getEmail());
        dashboard.setPhone(doctorDto.getPhone());
        dashboard.setSpecialization(doctorDto.getSpecialization());
        dashboard.setLicenseNumber(doctorDto.getLicenseNumber());
        dashboard.setMedicalDegree(doctorDto.getMedicalDegree());
        dashboard.setIsActive(doctorDto.getIsActive());
        return dashboard;
    }
}
