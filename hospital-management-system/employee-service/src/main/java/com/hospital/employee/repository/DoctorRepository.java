package com.hospital.employee.repository;

import com.hospital.employee.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    // Find by specialization
    List<Doctor> findBySpecialization(String specialization);
    
    // Find by specialization (case insensitive)
    List<Doctor> findBySpecializationIgnoreCase(String specialization);
    
    // Find active doctors
    List<Doctor> findByIsActiveTrue();
    
    // Find inactive doctors
    List<Doctor> findByIsActiveFalse();
    
    // Check if doctor exists by license number
    boolean existsByLicenseNumber(String licenseNumber);
    
    // Find by license number
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    
    // Find by medical degree
    List<Doctor> findByMedicalDegree(String medicalDegree);
    
    // Find doctors by department id
    @Query("SELECT d FROM Doctor d WHERE d.department.id = :departmentId")
    List<Doctor> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Find doctors by department name
    @Query("SELECT d FROM Doctor d WHERE d.department.name = :departmentName")
    List<Doctor> findByDepartmentName(@Param("departmentName") String departmentName);
    
    // Find available doctors by specialization
    @Query("SELECT d FROM Doctor d WHERE d.specialization = :specialization AND d.isActive = true")
    List<Doctor> findAvailableDoctorsBySpecialization(@Param("specialization") String specialization);
    
    // Find by matricule (inherited field)
    Optional<Doctor> findByMatricule(String matricule);
    
    // Check if matricule exists
    boolean existsByMatricule(String matricule);
    
    // Find by email
    Optional<Doctor> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
}
