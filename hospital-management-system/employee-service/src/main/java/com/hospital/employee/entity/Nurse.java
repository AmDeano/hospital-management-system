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
@DiscriminatorValue("NURSE")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Nurse extends Employee {

    @NotBlank(message = "Shift is required")
    @EqualsAndHashCode.Include
    private String shift; // DAY, NIGHT, ROTATING
    
    @EqualsAndHashCode.Include
    private String nursingLicense;

//    public Nurse(String shift, String nursingLicense) {
//        super();
//        this.shift = shift;
//        this.nursingLicense = nursingLicense;
//    }

    @Override
    public boolean canAccessPatientData() {
        return true;
    }

    @Override
    public boolean canPrescribeMedication() {
        return false;
    }

    @Override
    public boolean canScheduleAppointments() {
        return true;
    }

    @Override
    public List<String> getAvailableActions() {
        return Arrays.asList(
            "VIEW_PATIENTS", "UPDATE_PATIENT_VITALS", "SCHEDULE_APPOINTMENTS",
            "ADMINISTER_MEDICATION", "PATIENT_CARE_NOTES"
        );
    }

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
}