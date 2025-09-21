package com.hospital.employee.usecase;

import com.hospital.employee.entity.Doctor;
import com.hospital.employee.exception.DuplicateEmployeeException;
import com.hospital.employee.exception.InvalidEmployeeDataException;
import com.hospital.employee.repository.DoctorRepository;
import com.hospital.employee.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DoctorUseCaseImpl extends AbstractEmployeeUseCase<Doctor> {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    public DoctorUseCaseImpl(DoctorRepository doctorRepository,
                             DepartmentRepository departmentRepository) {
        super(doctorRepository);
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
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
}
