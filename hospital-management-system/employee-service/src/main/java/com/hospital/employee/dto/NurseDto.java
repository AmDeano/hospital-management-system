// NurseDto.java
package com.hospital.employee.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public class NurseDto extends EmployeeDto {
    
    @NotBlank(message = "Shift is required")
    private String shift;
    
    private String nursingLicense;

    // Default constructor
    public NurseDto() {
        super();
    }

    // Constructor with basic fields
    public NurseDto(String matricule, String firstName, String lastName, String email, String shift) {
        super(matricule, firstName, lastName, email);
        this.shift = shift;
    }

    // Constructor with all fields
    public NurseDto(String matricule, String firstName, String lastName, String email, 
                    String shift, String nursingLicense) {
        super(matricule, firstName, lastName, email);
        this.shift = shift;
        this.nursingLicense = nursingLicense;
    }

    // Getters and Setters
    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getNursingLicense() {
        return nursingLicense;
    }

    public void setNursingLicense(String nursingLicense) {
        this.nursingLicense = nursingLicense;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        
        NurseDto nurseDto = (NurseDto) obj;
        return Objects.equals(nursingLicense, nurseDto.nursingLicense);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nursingLicense);
    }

    @Override
    public String toString() {
        return "NurseDto{" +
                "matricule='" + getMatricule() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", shift='" + shift + '\'' +
                ", nursingLicense='" + nursingLicense + '\'' +
                ", isActive=" + getIsActive() +
                '}';
    }
}