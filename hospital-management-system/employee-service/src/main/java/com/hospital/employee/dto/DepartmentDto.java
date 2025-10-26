// DepartmentDto.java
package com.hospital.employee.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DepartmentDto {
    
    private Long id;
    
    @NotBlank(message = "Department name is required")
    private String name;
    
    private String supervisorMatricule; // simplified reference
    private List<String> employeeMatricules; // only matricule IDs
    private Integer employeeCount;

    // Default constructor
    public DepartmentDto() {
        this.employeeMatricules = new ArrayList<>();
        this.employeeCount = 0;
    }

    // Constructor with basic fields
    public DepartmentDto(String name) {
        this();
        this.name = name;
    }

    // Constructor with supervisor
    public DepartmentDto(String name, String supervisorMatricule) {
        this(name);
        this.supervisorMatricule = supervisorMatricule;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupervisorMatricule() {
        return supervisorMatricule;
    }

    public void setSupervisorMatricule(String supervisorMatricule) {
        this.supervisorMatricule = supervisorMatricule;
    }

    public List<String> getEmployeeMatricules() {
        return employeeMatricules;
    }

    public void setEmployeeMatricules(List<String> employeeMatricules) {
        this.employeeMatricules = employeeMatricules;
        this.employeeCount = employeeMatricules != null ? employeeMatricules.size() : 0;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    // Utility methods
    public void addEmployeeMatricule(String matricule) {
        if (employeeMatricules == null) {
            employeeMatricules = new ArrayList<>();
        }
        if (!employeeMatricules.contains(matricule)) {
            employeeMatricules.add(matricule);
            employeeCount = employeeMatricules.size();
        }
    }

    public void removeEmployeeMatricule(String matricule) {
        if (employeeMatricules != null) {
            employeeMatricules.remove(matricule);
            employeeCount = employeeMatricules.size();
        }
    }

    public boolean hasSupervisor() {
        return supervisorMatricule != null && !supervisorMatricule.trim().isEmpty();
    }

    public boolean hasEmployees() {
        return employeeMatricules != null && !employeeMatricules.isEmpty();
    }

    // equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        DepartmentDto that = (DepartmentDto) obj;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "DepartmentDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", supervisorMatricule='" + supervisorMatricule + '\'' +
                ", employeeCount=" + employeeCount +
                '}';
    }
}