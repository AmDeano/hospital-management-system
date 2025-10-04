package com.hospital.common.events;

import java.io.Serializable;
import java.util.Set;

public class EmployeeCreatedEvent implements Serializable {
    private Long id;
    private String matricule;
    private String email;
    private String firstName;
    private String lastName;
    private String passwordHash;
    private Set<String> roles;
    private String externalId;
    
    // Default constructor for serialization
    public EmployeeCreatedEvent() {}
    
    // Full constructor with correct parameter types
    public EmployeeCreatedEvent(Long id, String matricule, String email, 
                               String firstName, String lastName, 
                               String passwordHash, Set<String> roles, String externalId) {
        this.id = id;
        this.matricule = matricule;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.externalId = externalId;
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
}