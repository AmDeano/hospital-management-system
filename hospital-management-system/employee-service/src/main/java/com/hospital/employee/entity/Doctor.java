package com.hospital.employee.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Entity
@DiscriminatorValue("DOCTOR")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends Employee {

    @NotBlank(message = "Specialization is required")
    @EqualsAndHashCode.Include
    private String specialization;
    
    @NotBlank(message = "License number is required")
    @EqualsAndHashCode.Include
    private String licenseNumber;
    
    @NotBlank(message = "Medical degree is required")
    private String medicalDegree;

//    // Constructor with parameters (calls super constructor)
//    public Doctor(String specialization, String licenseNumber, String medicalDegree) {
//        super();
//        this.specialization = specialization;
//        this.licenseNumber = licenseNumber;
//        this.medicalDegree = medicalDegree;
//    }

    // Abstract method implementations
    @Override
    public boolean canAccessPatientData() {
        return true;
    }

    @Override
    public boolean canPrescribeMedication() {
        return true;
    }

    @Override
    public boolean canScheduleAppointments() {
        return true;
    }

    @Override
    public List<String> getAvailableActions() {
        return Arrays.asList(
            "VIEW_PATIENTS", "PRESCRIBE_MEDICATION", "SCHEDULE_APPOINTMENTS",
            "WRITE_MEDICAL_REPORTS", "ORDER_TESTS", "VIEW_MEDICAL_RECORDS"
        );
    }

    public String authorizePatientDischarge(Long patientId) {
    	return "Doctor " + getFullName() + "authorized discharge for patient" + patientId;
    }
    public String writeMedicaleCertificate(Long patientId) {
    	return "Doctor " + getFullName() + "issued a medical certifivate for patient " + patientId;
    }
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
}