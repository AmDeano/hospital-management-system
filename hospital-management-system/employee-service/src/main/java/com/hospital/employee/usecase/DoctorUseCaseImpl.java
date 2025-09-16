package com.hospital.employee.usecase;

import com.hospital.employee.entity.Doctor;
import com.hospital.employee.exception.DuplicateEmployeeException;
import com.hospital.employee.exception.EmployeeNotFoundException;
import com.hospital.employee.exception.InvalidEmployeeDataException;
import com.hospital.employee.repository.DepartmentRepository;
import com.hospital.employee.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DoctorUseCaseImpl implements EmployeeUseCase<Doctor> {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    public DoctorUseCaseImpl(DoctorRepository doctorRepository,
                             DepartmentRepository departmentRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Doctor create(Doctor doctor) {
        if (doctor.getSpecialization() == null || doctor.getSpecialization().trim().isEmpty()) {
            throw new InvalidEmployeeDataException("Doctor must have a specialization");
        }
        if (doctor.getLicenseNumber() == null || doctor.getLicenseNumber().trim().isEmpty()) {
            throw new InvalidEmployeeDataException("Doctor must have a valid license number");
        }
        if (doctorRepository.existsByLicenseNumber(doctor.getLicenseNumber())) {
            throw new DuplicateEmployeeException("Doctor with this license number already exists");
        }
        
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor update(Long id, Doctor doctor) {
        Doctor existing = findById(id);
        
        // Update fields properly using the getters/setters
        if (doctor.getFirstName() != null) {
            existing.setFirstName(doctor.getFirstName());
        }
        if (doctor.getLastName() != null) {
            existing.setLastName(doctor.getLastName());
        }
        if (doctor.getEmail() != null) {
            existing.setEmail(doctor.getEmail());
        }
        if (doctor.getPhone() != null) {
            existing.setPhone(doctor.getPhone());
        }
        if (doctor.getSpecialization() != null) {
            existing.setSpecialization(doctor.getSpecialization());
        }
        if (doctor.getLicenseNumber() != null) {
            existing.setLicenseNumber(doctor.getLicenseNumber());
        }
        if (doctor.getMedicalDegree() != null) {
            existing.setMedicalDegree(doctor.getMedicalDegree());
        }
        if (doctor.getDepartment() != null) {
            existing.setDepartment(doctor.getDepartment());
        }
        if (doctor.getAddress() != null) {
            existing.setAddress(doctor.getAddress());
        }
        if (doctor.getHireDate() != null) {
            existing.setHireDate(doctor.getHireDate());
        }
        if (doctor.getIsActive() != null) {
            existing.setIsActive(doctor.getIsActive());
        }
        
        return doctorRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Doctor doctor = findById(id);
        // Business rule placeholder: cannot delete if has active patients
        if (hasActivePatients(doctor)) {
            throw new InvalidEmployeeDataException("Cannot delete doctor with active patients");
        }
        doctorRepository.delete(doctor);
    }

    @Override
    public Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Doctor not found with id: " + id));
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    // Doctor-specific methods
    public List<Doctor> findBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> findAvailableDoctors() {
        return doctorRepository.findByIsActiveTrue();
    }

    private boolean hasActivePatients(Doctor doctor) {
        // TODO: integrate with patient service
        return false;
    }
}