package com.hospital.patient.repository;

import com.hospital.patient.entity.ConsultationFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationFeeRepository extends JpaRepository<ConsultationFee, Long> {

    List<ConsultationFee> findByDoctorId(Long doctorId);

    List<ConsultationFee> findBySpecialization(String specialization);

    List<ConsultationFee> findByDoctorIdAndIsActive(Long doctorId, Boolean isActive);

    Optional<ConsultationFee> findByDoctorIdAndSpecialization(Long doctorId, String specialization);

    @Query("SELECT cf FROM ConsultationFee cf WHERE cf.doctorId = :doctorId AND cf.isActive = true")
    List<ConsultationFee> findActiveFeesByDoctor(@Param("doctorId") Long doctorId);

    void deleteByDoctorId(Long doctorId);
}
