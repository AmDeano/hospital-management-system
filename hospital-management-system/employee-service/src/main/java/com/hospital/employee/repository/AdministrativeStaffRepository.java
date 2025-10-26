package com.hospital.employee.repository;

import com.hospital.employee.entity.AdministrativeStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministrativeStaffRepository extends JpaRepository<AdministrativeStaff, Long> {
    
    // Find by department area
    List<AdministrativeStaff> findByDepartmentArea(String departmentArea);
    
    // Find active staff
    List<AdministrativeStaff> findByIsActiveTrue();
    
    // Find inactive staff
    List<AdministrativeStaff> findByIsActiveFalse();
    
    // Find by department
    @Query("SELECT a FROM AdministrativeStaff a WHERE a.department.id = :departmentId")
    List<AdministrativeStaff> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Find by department name
    @Query("SELECT a FROM AdministrativeStaff a WHERE a.department.name = :departmentName")
    List<AdministrativeStaff> findByDepartmentName(@Param("departmentName") String departmentName);
    
    // Find by matricule
    Optional<AdministrativeStaff> findByMatricule(String matricule);
    
    // Check if matricule exists
    boolean existsByMatricule(String matricule);
    
    // Find by email
    Optional<AdministrativeStaff> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
}