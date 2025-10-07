package com.hospital.common.events;

import java.io.Serializable;
import java.util.Set;

public class EmployeeCreatedEvent implements Serializable {
    private Long id;
    private String matricule;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    //private Object object;
    private String passwordHash;
    private Set<String> roles;
    private String externalId;
    
 // Doctor fields
    private String specialization;
    private String licenseNumber;
    private String medicalDegree;
    
    // Nurse fields
    private String shift;
    private String nurseLicenseNumber;
    
    // Receptionist fields
    private String deskNumber;
    
    // Admin Staff fields
    private String departmentArea;
    
    // Observator fields
    private String assignedArea;
    
    // Default constructor for serialization
    public EmployeeCreatedEvent() {}
    
    // Full constructor with correct parameter types
    public EmployeeCreatedEvent(Long id, String matricule, String email, 
                               String firstName, String lastName, String phone, String address, 
                               String passwordHash, Set<String> roles, String externalId,
                               String specialization, String licenseNumber, String medicalDegree,
                               String shift, String nurseLicenseNumber,
                               String deskNumber, String departmentArea, String assignedArea) {
        this.id = id;
        this.matricule = matricule;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.externalId = externalId;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.medicalDegree = medicalDegree;
        this.shift = shift;
        this.nurseLicenseNumber = nurseLicenseNumber;
        this.deskNumber = deskNumber;
        this.departmentArea = departmentArea;
        this.assignedArea = assignedArea;
    }


	// Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMatricule() {
        return matricule;
    }
    
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
    public String getExternalId() {
        return externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getMedicalDegree() { return medicalDegree; }
    public void setMedicalDegree(String medicalDegree) { this.medicalDegree = medicalDegree; }
    
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
    
    public String getNurseLicenseNumber() { return nurseLicenseNumber; }
    public void setNurseLicenseNumber(String nurseLicenseNumber) { this.nurseLicenseNumber = nurseLicenseNumber; }
    
    public String getDeskNumber() { return deskNumber; }
    public void setDeskNumber(String deskNumber) { this.deskNumber = deskNumber; }
    
    public String getDepartmentArea() { return departmentArea; }
    public void setDepartmentArea(String departmentArea) { this.departmentArea = departmentArea; }
    
    public String getAssignedArea() { return assignedArea; }
    public void setAssignedArea(String assignedArea) { this.assignedArea = assignedArea; }
    
    public String getAddress () {
    	return address;
    }
    
    @Override
    public String toString() {
        return "EmployeeCreatedEvent{" +
                "id=" + id +
                ", matricule='" + matricule + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + roles +
                ", externalId='" + externalId + '\'' +
                '}';
    }

	public String getPhone() {
		return phone;
	}
}