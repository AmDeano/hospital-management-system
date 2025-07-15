package com.hospital.employee.entity;

/**
 * Enumeration for different types of employees in the hospital system
 */
public enum EmployeeType {
	
    ADMINISTRATION("Administration"),
    MEDICAL_STAFF("Medical Staff");

    private final String displayName;

    EmployeeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}