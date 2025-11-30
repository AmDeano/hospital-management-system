package com.hospital.employee.service;

import com.hospital.employee.dto.DoctorDashboardDto;
import com.hospital.employee.dto.DoctorDto;
import com.hospital.employee.dto.DoctorScheduleDto;
import com.hospital.employee.dto.DoctorAvailabilitySlotDto;
import com.hospital.employee.entity.Doctor;
import com.hospital.employee.exception.EmployeeNotFoundException;
import com.hospital.employee.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class DoctorDashboardService {

    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;

    public DoctorDashboardService(DoctorService doctorService, DoctorRepository doctorRepository) {
        this.doctorService = doctorService;
        this.doctorRepository = doctorRepository;
    }

    /**
     * Get complete doctor dashboard with all information and functionalities
     * Accessible via: /dashboard/doctors/{doctorMatricule}
     */
    public DoctorDashboardDto getDoctorDashboard(String doctorMatricule) {
        Doctor doctor = doctorRepository.findByMatricule(doctorMatricule)
                .orElseThrow(() -> new EmployeeNotFoundException("Doctor not found with matricule: " + doctorMatricule));

        DoctorDto doctorDto = DoctorDto.fromEntity(doctor);
        DoctorDashboardDto dashboard = DoctorDashboardDto.fromDoctor(doctorDto);

        // Add doctor's basic information
        dashboard.setDepartmentName(doctor.getDepartment() != null ? doctor.getDepartment().getName() : "N/A");

        // Add doctor's schedules
        List<DoctorScheduleDto> schedules = doctorService.getWeeklySchedule(doctor.getId());
        dashboard.setSchedules(schedules);

        // Add availability slots for next 30 days
        List<DoctorAvailabilitySlotDto> availabilitySlots = doctorService.getUpcomingAvailableSlots(doctor.getId(), 30);
        dashboard.setAvailabilitySlots(availabilitySlots);

        // TODO: Add counts from patient-service when integration is done
        dashboard.setTotalPatientsCount(0);
        dashboard.setAppointmentsTodayCount(0);
        dashboard.setPendingPrescriptionsCount(0);
        dashboard.setRecentMedicalRecordsCount(0);

        return dashboard;
    }

    /**
     * Get doctor's available time slots for a specific date
     */
    public List<DoctorAvailabilitySlotDto> getAvailableSlots(String doctorMatricule, LocalDate date) {
        Doctor doctor = doctorRepository.findByMatricule(doctorMatricule)
                .orElseThrow(() -> new EmployeeNotFoundException("Doctor not found with matricule: " + doctorMatricule));

        return doctorService.getAvailableSlotsForDate(doctor.getId(), date);
    }

    /**
     * Get doctor's personal information
     */
    public DoctorDto getDoctorInfo(String doctorMatricule) {
        Doctor doctor = doctorRepository.findByMatricule(doctorMatricule)
                .orElseThrow(() -> new EmployeeNotFoundException("Doctor not found with matricule: " + doctorMatricule));

        return DoctorDto.fromEntity(doctor);
    }
}
