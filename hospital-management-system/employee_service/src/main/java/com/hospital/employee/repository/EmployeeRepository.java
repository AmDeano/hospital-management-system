package com.hospital.employee.repository;

import com.hospital.employee.entity.Employee;
import com.hospital.employee.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    
    // Find by email
    Optional<Employee> findByEmail(String email);
    
    // Find by CIN
    Optional<Employee> findByCin(String cin);
    
    // Find by employee type
    List<Employee> findByEmployeeType(EmployeeType employeeType);
    
    // Find active employees
    List<Employee> findByIsActiveTrue();
    
    // Find inactive employees
    List<Employee> findByIsActiveFalse();
    
    // Find by department
    List<Employee> findByDepartement(String departement);
    
    // Find by department and employee type
    List<Employee> findByDepartementAndEmployeeType(String departement, EmployeeType employeeType);
    
    // Find by speciality (for medical staff)
    List<Employee> findBySpecialite(String specialite);
    
    // Find by supervisor
    List<Employee> findBySupervisorMatricule(String supervisorMatricule);
    
    // Find employees by name (case-insensitive)
    @Query("SELECT e FROM Employee e WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))")
    List<Employee> findByNomOrPrenomContainingIgnoreCase(@Param("nom") String nom, @Param("prenom") String prenom);
    
    // Find employees by full name
    @Query("SELECT e FROM Employee e WHERE LOWER(CONCAT(e.nom, ' ', e.prenom)) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    List<Employee> findByFullNameContainingIgnoreCase(@Param("fullName") String fullName);
    
    // Find employees hired in a specific date range
    List<Employee> findByDateEmbaucheBetween(LocalDate startDate, LocalDate endDate);
    
    // Find employees working on specific days
    @Query("SELECT e FROM Employee e JOIN e.workDays w WHERE w IN :workDays")
    List<Employee> findByWorkDaysIn(@Param("workDays") Set<WorkDay> workDays);
    
    // Find employees by shift time
    @Query("SELECT e FROM Employee e WHERE e.shiftStart = :shiftStart AND e.shiftEnd = :shiftEnd")
    List<Employee> findByShiftTime(@Param("shiftStart") String shiftStart, @Param("shiftEnd") String shiftEnd);
    
    // Find medical staff with specific speciality in a department
    @Query("SELECT e FROM Employee e WHERE e.employeeType = 'MEDICAL_STAFF' AND e.specialite = :specialite AND e.departement = :departement AND e.isActive = true")
    List<Employee> findActiveMedicalStaffBySpecialityAndDepartment(@Param("specialite") String specialite, @Param("departement") String departement);
    
    // Find available doctors for a specific day
    @Query("SELECT e FROM Employee e JOIN e.workDays w WHERE e.employeeType = 'MEDICAL_STAFF' AND e.poste LIKE '%Doctor%' AND w = :workDay AND e.isActive = true")
    List<Employee> findAvailableDoctorsByWorkDay(@Param("workDay") WorkDay workDay);
    
    // Count employees by type
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.employeeType = :employeeType AND e.isActive = true")
    Long countByEmployeeTypeAndIsActive(@Param("employeeType") EmployeeType employeeType);
    
    // Count employees by department
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.departement = :departement AND e.isActive = true")
    Long countByDepartementAndIsActive(@Param("departement") String departement);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if CIN exists
    boolean existsByCin(String cin);
    
    // Check if licence number exists
    boolean existsByLicenceNumber(String licenceNumber);
    
    // Find employees with expired licenses (assuming we have expiry date logic)
    @Query("SELECT e FROM Employee e WHERE e.employeeType = 'MEDICAL_STAFF' AND e.licenceNumber IS NOT NULL")
    List<Employee> findMedicalStaffWithLicence();
    
    // Search employees by multiple criteria
    @Query("SELECT e FROM Employee e WHERE " +
           "(:nom IS NULL OR LOWER(e.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:prenom IS NULL OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))) AND " +
           "(:departement IS NULL OR e.departement = :departement) AND " +
           "(:employeeType IS NULL OR e.employeeType = :employeeType) AND " +
           "(:isActive IS NULL OR e.isActive = :isActive)")
    List<Employee> searchEmployees(@Param("nom") String nom, 
                                 @Param("prenom") String prenom,
                                 @Param("departement") String departement,
                                 @Param("employeeType") EmployeeType employeeType,
                                 @Param("isActive") Boolean isActive);
    
    // Get employee statistics by department
    @Query("SELECT e.departement, COUNT(e), e.employeeType FROM Employee e WHERE e.isActive = true GROUP BY e.departement, e.employeeType")
    List<Object[]> getEmployeeStatisticsByDepartment();
    
    // Find employees without supervisor
    List<Employee> findBySupervisorMatriculeIsNull();
    
    // Find employees by phone number
    List<Employee> findByTelephone(String telephone);
    
    // Find employees by matricule pattern (for auto-generation)
    @Query("SELECT e FROM Employee e WHERE e.matricule LIKE :pattern ORDER BY e.matricule DESC")
    List<Employee> findByMatriculePattern(@Param("pattern") String pattern);
}