package com.hospital.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class EmployeeDto {
    
    @NotBlank(message = "Matricule is required")
    private String matricule;
    
    @NotBlank(message = "Nom is required")
    private String nom;
    
    @NotBlank(message = "Prenom is required")
    private String prenom;
    
    @NotBlank(message = "Poste is required")
    private String poste;
    
    @NotNull(message = "Employee type is required")
    private EmployeeType employeeType;
    
    private String departement;
    
    private String telephone;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @NotNull(message = "Hire date is required")
    @Past(message = "Hire date must be in the past")
    private LocalDate dateEmbauche;
    
    @Past(message = "Birth date must be in the past")
    private LocalDate dateNaissance;
    
    private String adresse;
    
    private String numeroSecuriteSociale;
    
    private String cin;
    
    // Medical staff specific fields
    private String specialite;
    private String licenceNumber;
    
    private Boolean isActive = true;
    
    private String supervisorMatricule;
    
    // Work schedule
    private Set<WorkDay> workDays;
    private String shiftStart;
    private String shiftEnd;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public EmployeeDto() {}

    public EmployeeDto(String matricule, String nom, String prenom, String poste, 
                      EmployeeType employeeType, String departement, String telephone, 
                      String email, LocalDate dateEmbauche, LocalDate dateNaissance,
                      String adresse, String numeroSecuriteSociale, String cin,
                      String specialite, String licenceNumber, Boolean isActive,
                      String supervisorMatricule, Set<WorkDay> workDays, 
                      String shiftStart, String shiftEnd,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.poste = poste;
        this.employeeType = employeeType;
        this.departement = departement;
        this.telephone = telephone;
        this.email = email;
        this.dateEmbauche = dateEmbauche;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.numeroSecuriteSociale = numeroSecuriteSociale;
        this.cin = cin;
        this.specialite = specialite;
        this.licenceNumber = licenceNumber;
        this.isActive = isActive;
        this.supervisorMatricule = supervisorMatricule;
        this.workDays = workDays;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNumeroSecuriteSociale() {
        return numeroSecuriteSociale;
    }

    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) {
        this.numeroSecuriteSociale = numeroSecuriteSociale;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getSupervisorMatricule() {
        return supervisorMatricule;
    }

    public void setSupervisorMatricule(String supervisorMatricule) {
        this.supervisorMatricule = supervisorMatricule;
    }

    public Set<WorkDay> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Set<WorkDay> workDays) {
        this.workDays = workDays;
    }

    public String getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(String shiftStart) {
        this.shiftStart = shiftStart;
    }

    public String getShiftEnd() {
        return shiftEnd;
    }

    public void setShiftEnd(String shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper method to get full name
    public String getFullName() {
        return nom + " " + prenom;
    }

    // Check if employee is medical staff
    public boolean isMedicalStaff() {
        return employeeType == EmployeeType.MEDICAL_STAFF;
    }

    // Check if employee is administration
    public boolean isAdministration() {
        return employeeType == EmployeeType.ADMINISTRATION;
    }
}

// Enum for Employee Type
enum EmployeeType {
    ADMINISTRATION,
    MEDICAL_STAFF
}

// Enum for Work Days
enum WorkDay {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}