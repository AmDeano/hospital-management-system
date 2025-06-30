package com.hospital.patient.repository;

import com.hospital.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    // Find patient by email
    Optional<Patient> findByEmail(String email);
    
    // Find patient by social security number
    Optional<Patient> findByNumeroSecuriteSociale(String numeroSecuriteSociale);
    
    // Find patients by name (case-insensitive)
    @Query("SELECT p FROM Patient p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Patient> findByNomContainingIgnoreCase(@Param("nom") String nom);
    
    // Find patients by phone number
    List<Patient> findByNumeroTelephone(String numeroTelephone);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if social security number exists
    boolean existsByNumeroSecuriteSociale(String numeroSecuriteSociale);
}