package com.hospital.patient.repository;

import com.hospital.patient.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatientId(String patientId);

    List<MedicalRecord> findByDoctorId(Long doctorId);

    List<MedicalRecord> findByPatientIdOrderByVisitDateDesc(String patientId);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.doctorId = :doctorId ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findDoctorMedicalRecords(@Param("doctorId") Long doctorId);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patientId = :patientId AND mr.visitDate >= :fromDate AND mr.visitDate <= :toDate ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findPatientRecordsByDateRange(
            @Param("patientId") String patientId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patientId = :patientId ORDER BY mr.visitDate DESC LIMIT 1")
    MedicalRecord findLatestPatientRecord(@Param("patientId") String patientId);

    void deleteByPatientId(String patientId);
}
