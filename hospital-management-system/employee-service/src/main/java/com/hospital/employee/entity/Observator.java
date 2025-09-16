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
@DiscriminatorValue("OBSERVATOR")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Observator extends Employee {

    @NotBlank(message = "Assigned area is required")
    @EqualsAndHashCode.Include
    private String assignedArea;

//    public Observator(String assignedArea) {
//        super();
//        this.assignedArea = assignedArea;
//    }

    @Override
    public boolean canAccessPatientData() {
        return false;
    }

    @Override
    public boolean canPrescribeMedication() {
        return false;
    }

    @Override
    public boolean canScheduleAppointments() {
        return false;
    }

    @Override
    public List<String> getAvailableActions() {
        return Arrays.asList("VIEW_PUBLIC_DASHBOARDS");
    }

	public String getAssignedArea() {
		return assignedArea;
	}

	public void setAssignedArea(String assignedArea) {
		this.assignedArea = assignedArea;
		
	}
}