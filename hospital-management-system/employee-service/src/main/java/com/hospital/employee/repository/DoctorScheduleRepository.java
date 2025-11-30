package com.hospital.employee.repository;

import com.hospital.employee.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    List<DoctorSchedule> findByDoctorId(Long doctorId);

    Optional<DoctorSchedule> findByDoctorIdAndDayOfWeek(Long doctorId, DoctorSchedule.DayOfWeek dayOfWeek);

    List<DoctorSchedule> findByDoctorIdAndIsAvailable(Long doctorId, Boolean isAvailable);

    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor.id = :doctorId ORDER BY FIELD(ds.dayOfWeek, 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')")
    List<DoctorSchedule> findDoctorWeeklySchedule(@Param("doctorId") Long doctorId);

    void deleteByDoctorId(Long doctorId);
}
