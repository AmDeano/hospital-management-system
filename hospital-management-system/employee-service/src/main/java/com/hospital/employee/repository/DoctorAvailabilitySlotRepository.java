package com.hospital.employee.repository;

import com.hospital.employee.entity.DoctorAvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorAvailabilitySlotRepository extends JpaRepository<DoctorAvailabilitySlot, Long> {

    List<DoctorAvailabilitySlot> findByDoctorId(Long doctorId);

    List<DoctorAvailabilitySlot> findByDoctorIdAndAvailableDate(Long doctorId, LocalDate availableDate);

    List<DoctorAvailabilitySlot> findByDoctorIdAndAvailableDateAndIsAvailable(Long doctorId, LocalDate availableDate, Boolean isAvailable);

    @Query("SELECT das FROM DoctorAvailabilitySlot das WHERE das.doctor.id = :doctorId AND das.availableDate >= :fromDate AND das.availableDate <= :toDate ORDER BY das.availableDate, das.startTime")
    List<DoctorAvailabilitySlot> findAvailableSlotsByDateRange(
            @Param("doctorId") Long doctorId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("SELECT das FROM DoctorAvailabilitySlot das WHERE das.doctor.id = :doctorId AND das.availableDate >= :fromDate AND das.isAvailable = true ORDER BY das.availableDate, das.startTime")
    List<DoctorAvailabilitySlot> findUpcomingAvailableSlots(
            @Param("doctorId") Long doctorId,
            @Param("fromDate") LocalDate fromDate
    );

    void deleteByDoctorId(Long doctorId);
}
