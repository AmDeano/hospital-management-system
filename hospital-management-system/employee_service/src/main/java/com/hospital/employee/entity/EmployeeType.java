package com.hospital.employee.entity;

/**
 * Enumeration for different types of employees in the hospital system
 */
/*public enum EmployeeType {
	
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
}*/
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

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

    @JsonValue
    public String toJson() {
        return name();
    }

    @Override
    public String toString() {
        return displayName;
    }

    @JsonCreator
    public static EmployeeType fromJson(String value) {
        return EmployeeType.valueOf(value.toUpperCase());
    }
}
