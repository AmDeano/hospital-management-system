package com.hospital.employee.repository;

import com.hospital.employee.entity.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist, Long> {
    
    // Find by desk number
    Optional<Receptionist> findByDeskNumber(String deskNumber);
    
    // Check if desk number exists
    boolean existsByDeskNumber(String deskNumber);
    
    // Find active receptionists
    List<Receptionist> findByIsActiveTrue();
    
    // Find inactive receptionists
    List<Receptionist> findByIsActiveFalse();
    
    // Find by department
    @Query("SELECT r FROM Receptionist r WHERE r.department.id = :departmentId")
    List<Receptionist> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Find by department name
    @Query("SELECT r FROM Receptionist r WHERE r.department.name = :departmentName")
    List<Receptionist> findByDepartmentName(@Param("departmentName") String departmentName);
    
    // Find by matricule
    Optional<Receptionist> findByMatricule(String matricule);
    
    // Check if matricule exists
    boolean existsByMatricule(String matricule);
    
    // Find by email
    Optional<Receptionist> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
}