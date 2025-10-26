package com.hospital.employee.repository;

import com.hospital.employee.entity.Employee;
import com.hospital.employee.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // Find by matricule (unique identifier)
    Optional<Employee> findByMatricule(String matricule);
    
    // Check if matricule exists
    boolean existsByMatricule(String matricule);
    
    // Find by email
    Optional<Employee> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find active employees
    List<Employee> findByIsActiveTrue();
    
    // Find inactive employees
    List<Employee> findByIsActiveFalse();
    
    // Find by role
    List<Employee> findByRole(Role role);
    
    // Find by department
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId")
    List<Employee> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Find by department name
    @Query("SELECT e FROM Employee e WHERE e.department.name = :departmentName")
    List<Employee> findByDepartmentName(@Param("departmentName") String departmentName);
    
    // Find by hire date range
    @Query("SELECT e FROM Employee e WHERE e.hireDate BETWEEN :startDate AND :endDate")
    List<Employee> findByHireDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Search by name (first or last name)
    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Employee> searchByName(@Param("name") String name);
    
    // Find by phone
    Optional<Employee> findByPhone(String phone);
    
    // Check if phone exists
    boolean existsByPhone(String phone);
    
    // Count active employees
    long countByIsActiveTrue();
    
    // Count inactive employees
    long countByIsActiveFalse();
    
    // Count by role
    long countByRole(Role role);
    
    // Count by department
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    long countByDepartmentId(@Param("departmentId") Long departmentId);
}