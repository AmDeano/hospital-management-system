package com.hospital.patient.repository;

import com.hospital.patient.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatientId(String patientId);

    List<Prescription> findByDoctorId(Long doctorId);

    List<Prescription> findByPatientIdAndIsActive(String patientId, Boolean isActive);

    List<Prescription> findByDoctorIdAndIsActive(Long doctorId, Boolean isActive);

    @Query("SELECT p FROM Prescription p WHERE p.patientId = :patientId ORDER BY p.createdAt DESC")
    List<Prescription> findPatientPrescriptionHistory(@Param("patientId") String patientId);

    @Query("SELECT p FROM Prescription p WHERE p.doctorId = :doctorId ORDER BY p.createdAt DESC")
    List<Prescription> findDoctorPrescriptions(@Param("doctorId") Long doctorId);

    void deleteByPatientId(String patientId);
}
