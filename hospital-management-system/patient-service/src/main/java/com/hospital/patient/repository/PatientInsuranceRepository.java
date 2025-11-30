package com.hospital.patient.repository;

import com.hospital.patient.entity.PatientInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientInsuranceRepository extends JpaRepository<PatientInsurance, Long> {

    Optional<PatientInsurance> findByPatientId(String patientId);

    @Query("SELECT pi FROM PatientInsurance pi WHERE pi.patientId = :patientId AND pi.isActive = true")
    Optional<PatientInsurance> findActiveInsuranceByPatientId(@Param("patientId") String patientId);

    void deleteByPatientId(String patientId);
}
