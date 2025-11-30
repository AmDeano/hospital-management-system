package com.hospital.patient.repository;

import com.hospital.patient.entity.DiagnosticReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiagnosticReportRepository extends JpaRepository<DiagnosticReport, Long> {

    List<DiagnosticReport> findByPatientId(String patientId);

    List<DiagnosticReport> findByDoctorId(Long doctorId);

    List<DiagnosticReport> findByTestId(Long testId);

    List<DiagnosticReport> findByStatus(DiagnosticReport.ReportStatus status);

    List<DiagnosticReport> findByPatientIdOrderByReportDateDesc(String patientId);

    @Query("SELECT dr FROM DiagnosticReport dr WHERE dr.patientId = :patientId AND dr.status = :status ORDER BY dr.reportDate DESC")
    List<DiagnosticReport> findPatientReportsByStatus(
            @Param("patientId") String patientId,
            @Param("status") DiagnosticReport.ReportStatus status
    );

    @Query("SELECT dr FROM DiagnosticReport dr WHERE dr.doctorId = :doctorId ORDER BY dr.reportDate DESC")
    List<DiagnosticReport> findDoctorDiagnosticReports(@Param("doctorId") Long doctorId);

    @Query("SELECT dr FROM DiagnosticReport dr WHERE dr.patientId = :patientId AND dr.reportDate >= :fromDate AND dr.reportDate <= :toDate ORDER BY dr.reportDate DESC")
    List<DiagnosticReport> findPatientReportsByDateRange(
            @Param("patientId") String patientId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    void deleteByPatientId(String patientId);
}
