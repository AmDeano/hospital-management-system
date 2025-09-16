package com.hospital.employee.repository;

import com.hospital.employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    // Find by name
    Optional<Department> findByName(String name);
    
    // Check if name exists
    boolean existsByName(String name);
    
    // Find by name (case insensitive)
    Optional<Department> findByNameIgnoreCase(String name);
    
    // Find departments with supervisor
    @Query("SELECT d FROM Department d WHERE d.supervisor IS NOT NULL")
    List<Department> findDepartmentsWithSupervisor();
    
    // Find departments without supervisor
    @Query("SELECT d FROM Department d WHERE d.supervisor IS NULL")
    List<Department> findDepartmentsWithoutSupervisor();
    
    // Find departments by supervisor matricule
    @Query("SELECT d FROM Department d WHERE d.supervisor.matricule = :matricule")
    List<Department> findBySupervisorMatricule(@Param("matricule") String matricule);
    
    // Count employees in department
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    long countEmployeesInDepartment(@Param("departmentId") Long departmentId);
    
    // Find departments with employee count
    @Query("SELECT d, COUNT(e) FROM Department d LEFT JOIN d.employees e GROUP BY d")
    List<Object[]> findDepartmentsWithEmployeeCount();
}