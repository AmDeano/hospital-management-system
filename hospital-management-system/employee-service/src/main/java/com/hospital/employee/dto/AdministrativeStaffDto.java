// AdministrativeStaffDto.java
package com.hospital.employee.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public class AdministrativeStaffDto extends EmployeeDto {
    
    @NotBlank(message = "Department area is required")
    private String departmentArea;

    // Default constructor
    public AdministrativeStaffDto() {
        super();
    }

    // Constructor with basic fields
    public AdministrativeStaffDto(String matricule, String firstName, String lastName, String email, String departmentArea) {
        super(matricule, firstName, lastName, email);
        this.departmentArea = departmentArea;
    }

    // Getters and Setters
    public String getDepartmentArea() {
        return departmentArea;
    }

    public void setDepartmentArea(String departmentArea) {
        this.departmentArea = departmentArea;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        
        AdministrativeStaffDto staffDto = (AdministrativeStaffDto) obj;
        return Objects.equals(departmentArea, staffDto.departmentArea);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), departmentArea);
    }

    @Override
    public String toString() {
        return "AdministrativeStaffDto{" +
                "matricule='" + getMatricule() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", departmentArea='" + departmentArea + '\'' +
                ", isActive=" + getIsActive() +
                '}';
    }
}