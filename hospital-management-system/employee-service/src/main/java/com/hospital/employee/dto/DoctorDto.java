// DoctorDto.java
package com.hospital.employee.dto;

import com.hospital.employee.entity.Doctor;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public class DoctorDto extends EmployeeDto {

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private String medicalDegree;

    // Default constructor
    public DoctorDto() {
        super();
    }

    // Constructor with basic fields
    public DoctorDto(String matricule, String firstName, String lastName, String email, String specialization) {
        super(matricule, firstName, lastName, email);
        this.specialization = specialization;
    }

    // Constructor with all fields
    public DoctorDto(String matricule, String firstName, String lastName, String email, 
                     String specialization, String licenseNumber, String medicalDegree) {
        super(matricule, firstName, lastName, email);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.medicalDegree = medicalDegree;
    }

    // Getters and Setters
    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getMedicalDegree() {
        return medicalDegree;
    }

    public void setMedicalDegree(String medicalDegree) {
        this.medicalDegree = medicalDegree;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        
        DoctorDto doctorDto = (DoctorDto) obj;
        return Objects.equals(licenseNumber, doctorDto.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), licenseNumber);
    }

    @Override
    public String toString() {
        return "DoctorDto{" +
                "matricule='" + getMatricule() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", medicalDegree='" + medicalDegree + '\'' +
                ", isActive=" + getIsActive() +
                '}';
    }

    /**
     * Convert Doctor entity to DoctorDto
     */
    public static DoctorDto fromEntity(Doctor doctor) {
        DoctorDto dto = new DoctorDto();
        dto.setMatricule(doctor.getMatricule());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setEmail(doctor.getEmail());
        dto.setPhone(doctor.getPhone());
        dto.setAddress(doctor.getAddress());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setMedicalDegree(doctor.getMedicalDegree());
        dto.setIsActive(doctor.getIsActive());
        return dto;
    }
}