package com.hospital.employee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @Column(name = "matricule", nullable = false, unique = true)
    private String matricule;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Prenom is required")
    @Column(nullable = false)
    private String prenom;

    @NotBlank(message = "Poste is required")
    private String poste;

    @NotNull(message = "Employee type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type", nullable = false)
    private EmployeeType employeeType;

    private String departement;

    @Column(name = "telephone")
    private String telephone;

    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @NotNull(message = "Hire date is required")
    @Column(name = "date_embauche", nullable = false)
    private LocalDate dateEmbauche;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    private String adresse;

    @Column(name = "numero_securite_sociale")
    private String numeroSecuriteSociale;

    @Column(name = "cin", unique = true)
    private String cin;

    // Medical staff specific fields
    @Column(name = "specialite")
    private String specialite; // For doctors

    @Column(name = "licence_number")
    private String licenceNumber; // Professional license

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "supervisor_matricule")
    private String supervisorMatricule;

    // Work schedule
    @ElementCollection
    @CollectionTable(name = "employee_work_days", joinColumns = @JoinColumn(name = "employee_matricule"))
    @Column(name = "work_day")
    @Enumerated(EnumType.STRING)
    private Set<WorkDay> workDays;

    @Column(name = "shift_start")
    private String shiftStart; // e.g., "08:00"

    @Column(name = "shift_end")
    private String shiftEnd; // e.g., "17:00"

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Employee() {}

    public Employee(String matricule, String nom, String prenom, String poste, 
                   EmployeeType employeeType, String departement, String telephone, 
                   String email, LocalDate dateEmbauche) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.poste = poste;
        this.employeeType = employeeType;
        this.departement = departement;
        this.telephone = telephone;
        this.email = email;
        this.dateEmbauche = dateEmbauche;
        this.isActive = true;
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