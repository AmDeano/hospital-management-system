package com.hospital.employee.repository;

import com.hospital.employee.entity.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, Long> {
    
    // Find by shift
    List<Nurse> findByShift(String shift);
    
    // Find active nurses
    List<Nurse> findByIsActiveTrue();
    
    // Find inactive nurses
    List<Nurse> findByIsActiveFalse();
    
    // Find by nursing license
    Optional<Nurse> findByNursingLicense(String nursingLicense);
    
    // Check if nursing license exists
    boolean existsByNursingLicense(String nursingLicense);
    
    // Find nurses by department
    @Query("SELECT n FROM Nurse n WHERE n.department.id = :departmentId")
    List<Nurse> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Find nurses by department name
    @Query("SELECT n FROM Nurse n WHERE n.department.name = :departmentName")
    List<Nurse> findByDepartmentName(@Param("departmentName") String departmentName);
    
    // Find active nurses by shift
    @Query("SELECT n FROM Nurse n WHERE n.shift = :shift AND n.isActive = true")
    List<Nurse> findActiveNursesByShift(@Param("shift") String shift);
    
    // Find by matricule
    Optional<Nurse> findByMatricule(String matricule);
    
    // Check if matricule exists
    boolean existsByMatricule(String matricule);
    
    // Find by email
    Optional<Nurse> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
}