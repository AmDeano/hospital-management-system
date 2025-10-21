// ReceptionistDto.java
package com.hospital.employee.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public class ReceptionistDto extends EmployeeDto {
    
    @NotBlank(message = "Desk number is required")
    private String deskNumber;

    // Default constructor
    public ReceptionistDto() {
        super();
    }

    // Constructor with basic fields
    public ReceptionistDto(String matricule, String firstName, String lastName, String email, String deskNumber) {
        super(matricule, firstName, lastName, email);
        this.deskNumber = deskNumber;
    }

    // Getters and Setters
    public String getDeskNumber() {
        return deskNumber;
    }

    public void setDeskNumber(String deskNumber) {
        this.deskNumber = deskNumber;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj) || getClass() != obj.getClass()) return false;
        
        ReceptionistDto that = (ReceptionistDto) obj;
        return Objects.equals(deskNumber, that.deskNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deskNumber);
    }

    @Override
    public String toString() {
        return "ReceptionistDto{" +
                "matricule='" + getMatricule() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", deskNumber='" + deskNumber + '\'' +
                ", isActive=" + getIsActive() +
                '}';
    }
}