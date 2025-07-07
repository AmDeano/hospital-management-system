package com.hospital.patient.repository;

import com.hospital.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    
    // Find patient by email
    Optional<Patient> findByEmail(String email);
    
    // Find patient by CIN
    Optional<Patient> findByCin(String cin);
    
    // Find patient by social security number
    Optional<Patient> findByNumeroSecuriteSociale(String numeroSecuriteSociale);
    
    // Find patients by name (case-insensitive)
    @Query("SELECT p FROM Patient p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Patient> findByNomContainingIgnoreCase(@Param("nom") String nom);
    
    // Find patients by phone number
    List<Patient> findByNumeroTelephone(String numeroTelephone);
    
    // Find minors by parent CIN
    List<Patient> findByParentCin(String parentCin);
    
    // Find all minors
    List<Patient> findByIsMinorTrue();
    
    // Find all adults
    List<Patient> findByIsMinorFalse();
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if CIN exists
    boolean existsByCin(String cin);
    
    // Check if social security number exists
    boolean existsByNumeroSecuriteSociale(String numeroSecuriteSociale);
    
    // Get next auto-generated ID for minors
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(p.id, 6) AS int)), 0) + 1 FROM Patient p WHERE p.id LIKE 'MIN-%'")
    Integer getNextMinorId();
}