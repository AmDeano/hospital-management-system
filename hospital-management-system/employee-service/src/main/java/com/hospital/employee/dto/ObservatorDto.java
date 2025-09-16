// ObservatorDto.java
package com.hospital.employee.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public class ObservatorDto extends EmployeeDto {
    
    @NotBlank(message = "Assigned area is required")
    private String assignedArea;

    // Default constructor
    public ObservatorDto() {
        super();
    }

    // Constructor with basic fields
    public ObservatorDto(String matricule, String firstName, String lastName, String email, String assignedArea) {
        super(matricule, firstName, lastName, email);
        this.assignedArea = assignedArea;
    }

    // Getters and Setters
    public String getAssignedArea() {
        return assignedArea;
    }

    public void setAssignedArea(String assignedArea) {
        this.assignedArea = assignedArea;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj) || getClass() != obj.getClass()) return false;
        
        ObservatorDto that = (ObservatorDto) obj;
        return Objects.equals(assignedArea, that.assignedArea);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), assignedArea);
    }

    @Override
    public String toString() {
        return "ObservatorDto{" +
                "matricule='" + getMatricule() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", assignedArea='" + assignedArea + '\'' +
                ", isActive=" + getIsActive() +
                '}';
    }
}