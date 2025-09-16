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
@DiscriminatorValue("ADMINISTRATIVE_STAFF")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdministrativeStaff extends Employee {

    @NotBlank(message = "Department area is required")
    @EqualsAndHashCode.Include
    private String departmentArea; // HR, Logistics, Materials

//    public AdministrativeStaff(String departmentArea) {
//        super();
//        this.departmentArea = departmentArea;
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
        return Arrays.asList("MANAGE_STAFF_FILES", "ORDER_SUPPLIES");
    }
}