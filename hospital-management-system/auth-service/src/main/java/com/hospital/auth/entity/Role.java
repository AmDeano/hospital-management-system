// auth-service/src/main/java/com/hospital/auth/entity/Role.java
package com.hospital.auth.entity;

public enum Role {
  ADMIN("Administrator"),
  HR("Human Resources"), 
  DOCTOR("Doctor"),
  NURSE("Nurse"),
  RECEPTIONIST("Receptionist"),
  OBSERVATOR("observator"),
  SUPERVISOR("supervisor"),
  PATIENT("Patient");

  private final String displayName;

  Role(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  // Check if role is employee role (non-patient)
  public boolean isEmployeeRole() {
    return this != PATIENT;
  }

  // Check if role has admin privileges
  public boolean hasAdminPrivileges() {
    return this == ADMIN;
  }

  // Check if role can manage employees
  public boolean canManageEmployees() {
    return this == ADMIN || this == HR;
  }
}