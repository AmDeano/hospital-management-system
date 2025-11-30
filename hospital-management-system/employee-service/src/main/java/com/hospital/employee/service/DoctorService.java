package com.hospital.employee.service;

import com.hospital.employee.entity.Doctor;
import com.hospital.employee.entity.DoctorSchedule;
import com.hospital.employee.entity.DoctorAvailabilitySlot;
import com.hospital.employee.dto.DoctorScheduleDto;
import com.hospital.employee.dto.DoctorAvailabilitySlotDto;
import com.hospital.employee.exception.DuplicateEmployeeException;
import com.hospital.employee.exception.InvalidEmployeeDataException;
import com.hospital.employee.repository.DoctorRepository;
import com.hospital.employee.repository.DoctorScheduleRepository;
import com.hospital.employee.repository.DoctorAvailabilitySlotRepository;
import com.hospital.employee.repository.DepartmentRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorService extends AbstractEmployeeService<Doctor> {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorAvailabilitySlotRepository availabilityRepository;
    private final RabbitTemplate rabbitTemplate;

    public DoctorService(DoctorRepository doctorRepository,
                             DepartmentRepository departmentRepository,
                             DoctorScheduleRepository scheduleRepository,
                             DoctorAvailabilitySlotRepository availabilityRepository,
                             RabbitTemplate rabbitTemplate) {
        super(doctorRepository, rabbitTemplate);
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.availabilityRepository = availabilityRepository;
		this.rabbitTemplate = rabbitTemplate;
    }
    
    @Override
    protected Set<String> determineRoles(Doctor doctor) {
        return Set.of("DOCTOR", "EMPLOYEE");
    }

    @Override
    protected void validateOnCreate(Doctor doctor) {
        if (doctor.getSpecialization() == null || doctor.getSpecialization().isBlank()) {
            throw new InvalidEmployeeDataException("Doctor must have a specialization");
        }
        if (doctor.getLicenseNumber() == null || doctor.getLicenseNumber().isBlank()) {
            throw new InvalidEmployeeDataException("Doctor must have a valid license number");
        }
        if (doctorRepository.existsByLicenseNumber(doctor.getLicenseNumber())) {
            throw new DuplicateEmployeeException("Doctor with this license number already exists");
        }
    }

    @Override
    protected void updateSpecificFields(Doctor existing, Doctor update) {
        if (update.getSpecialization() != null) existing.setSpecialization(update.getSpecialization());
        if (update.getLicenseNumber() != null) existing.setLicenseNumber(update.getLicenseNumber());
        if (update.getMedicalDegree() != null) existing.setMedicalDegree(update.getMedicalDegree());
    }

    @Override
    protected void beforeDelete(Doctor doctor) {
        if (hasActivePatients(doctor)) {
            throw new InvalidEmployeeDataException("Cannot delete doctor with active patients");
        }
    }

    // Queries
    public List<Doctor> findBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> findAvailableDoctors() {
        return doctorRepository.findByIsActiveTrue();
    }

    // Doctor-specific actions
    public String authorizePatientDischarge(Long doctorId, Long patientId) {
        Doctor doctor = findById(doctorId);
        return "Doctor " + doctor.getFullName() + " authorized discharge for patient " + patientId;
    }

    public String writeMedicalCertificate(Long doctorId, Long patientId, String details) {
        Doctor doctor = findById(doctorId);
        return "Doctor " + doctor.getFullName() +
               " issued medical certificate for patient " + patientId + ": " + details;
    }

    private boolean hasActivePatients(Doctor doctor) {
        // TODO integrate with patient service
        return false;
    }

    // Doctor Schedule Methods
    public List<DoctorScheduleDto> getDoctorSchedules(Long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(DoctorScheduleDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DoctorScheduleDto> getWeeklySchedule(Long doctorId) {
        return scheduleRepository.findDoctorWeeklySchedule(doctorId).stream()
                .map(DoctorScheduleDto::fromEntity)
                .collect(Collectors.toList());
    }

    public DoctorScheduleDto createOrUpdateSchedule(Long doctorId, DoctorScheduleDto scheduleDto) {
        Doctor doctor = findById(doctorId);
        DoctorSchedule schedule = scheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, scheduleDto.getDayOfWeek())
                .orElse(new DoctorSchedule());

        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(scheduleDto.getDayOfWeek());
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        schedule.setIsAvailable(scheduleDto.getIsAvailable());

        schedule = scheduleRepository.save(schedule);
        return DoctorScheduleDto.fromEntity(schedule);
    }

    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }

    // Doctor Availability Slot Methods
    public List<DoctorAvailabilitySlotDto> getDoctorAvailability(Long doctorId) {
        return availabilityRepository.findByDoctorId(doctorId).stream()
                .map(DoctorAvailabilitySlotDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DoctorAvailabilitySlotDto> getAvailableSlotsForDate(Long doctorId, LocalDate date) {
        return availabilityRepository.findByDoctorIdAndAvailableDateAndIsAvailable(doctorId, date, true).stream()
                .map(DoctorAvailabilitySlotDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DoctorAvailabilitySlotDto> getUpcomingAvailableSlots(Long doctorId, Integer daysAhead) {
        LocalDate fromDate = LocalDate.now();
        return availabilityRepository.findUpcomingAvailableSlots(doctorId, fromDate).stream()
                .filter(slot -> slot.getAvailableDate().isBefore(fromDate.plusDays(daysAhead)))
                .map(DoctorAvailabilitySlotDto::fromEntity)
                .collect(Collectors.toList());
    }

    public DoctorAvailabilitySlotDto createAvailabilitySlot(Long doctorId, DoctorAvailabilitySlotDto slotDto) {
        Doctor doctor = findById(doctorId);
        DoctorAvailabilitySlot slot = DoctorAvailabilitySlot.builder()
                .doctor(doctor)
                .availableDate(slotDto.getAvailableDate())
                .startTime(slotDto.getStartTime())
                .endTime(slotDto.getEndTime())
                .isAvailable(slotDto.getIsAvailable() != null ? slotDto.getIsAvailable() : true)
                .notes(slotDto.getNotes())
                .build();

        slot = availabilityRepository.save(slot);
        return DoctorAvailabilitySlotDto.fromEntity(slot);
    }

    public void deleteAvailabilitySlot(Long slotId) {
        availabilityRepository.deleteById(slotId);
    }

    public void markSlotAsBooked(Long slotId) {
        availabilityRepository.findById(slotId).ifPresent(slot -> {
            slot.setIsAvailable(false);
            availabilityRepository.save(slot);
        });
    }
}
