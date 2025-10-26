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
@DiscriminatorValue("RECEPTIONIST")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Receptionist extends Employee {

    @NotBlank(message = "Desk number is required")
    @EqualsAndHashCode.Include
    private String deskNumber;

//    public Receptionist(String deskNumber) {
//        super();
//        this.deskNumber = deskNumber;
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
        return true;
    }

    @Override
    public List<String> getAvailableActions() {
        return Arrays.asList(
            "SCHEDULE_APPOINTMENTS", "CHECK_IN_PATIENTS",
            "HANDLE_PAYMENTS", "MANAGE_WAITING_LIST"
        );
    }

	public String getDeskNumber() {
		return deskNumber;
	}

	public void setDeskNumber(String deskNumber) {
		this.deskNumber = deskNumber;
		
	}
}