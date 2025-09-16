package com.hospital.employee.repository;

import com.hospital.employee.entity.Observator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObservatorRepository extends JpaRepository<Observator, Long> {
    
    // Find by assigned area
    List<Observator> findByAssignedArea(String assignedArea);
    
    // Find active observators
    List<Observator> findByIsActiveTrue();
    
    // Find inactive observators
    List<Observator> findByIsActiveFalse();
    
    // Find by department
    @Query("SELECT o FROM Observator o WHERE o.department.id = :departmentId")
    List<Observator> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Find by department name
    @Query("SELECT o FROM Observator o WHERE o.department.name = :departmentName")
    List<Observator> findByDepartmentName(@Param("departmentName") String departmentName);
    
    // Find by matricule
    Optional<Observator> findByMatricule(String matricule);
    
    // Check if matricule exists
    boolean existsByMatricule(String matricule);
    
    // Find by email
    Optional<Observator> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
}