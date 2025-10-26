package com.hospital.patient.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity
@Table(name = "patients")
public class Patient {
    
    @Id
    private String id; // CIN or auto-generated ID for minors
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String nom;
    
    @Past(message = "Birth date must be in the past")
    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @Column(name = "numero_telephone")
    private String numeroTelephone;
    
    private String adresse;
    
    @Column(name = "numero_securite_sociale")
    private String numeroSecuriteSociale;
    
    @Column(name = "cin")
    private String cin; // CIN for adults
    
    @Column(name = "is_minor")
    private Boolean isMinor;
    
    @Column(name = "parent_cin")
    private String parentCin; // CIN of parent/guardian for minors
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Calculate if patient is minor based on birth date
        if (dateNaissance != null) {
            int age = Period.between(dateNaissance, LocalDate.now()).getYears();
            isMinor = age < 18;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Patient() {}
    
    public Patient(String nom, LocalDate dateNaissance, String email, 
                   String numeroTelephone, String adresse, String numeroSecuriteSociale,
                   String cin, String parentCin) {
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.numeroTelephone = numeroTelephone;
        this.adresse = adresse;
        this.numeroSecuriteSociale = numeroSecuriteSociale;
        this.cin = cin;
        this.parentCin = parentCin;
        
        // Calculate if patient is minor
        if (dateNaissance != null) {
            int age = Period.between(dateNaissance, LocalDate.now()).getYears();
            this.isMinor = age < 18;
        }
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { 
        this.dateNaissance = dateNaissance;
        // Recalculate minor status when birth date changes
        if (dateNaissance != null) {
            int age = Period.between(dateNaissance, LocalDate.now()).getYears();
            this.isMinor = age < 18;
        }
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNumeroTelephone() { return numeroTelephone; }
    public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    public String getNumeroSecuriteSociale() { return numeroSecuriteSociale; }
    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) { this.numeroSecuriteSociale = numeroSecuriteSociale; }
    
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    
    public Boolean getIsMinor() { return isMinor; }
    public void setIsMinor(Boolean isMinor) { this.isMinor = isMinor; }
    
    public String getParentCin() { return parentCin; }
    public void setParentCin(String parentCin) { this.parentCin = parentCin; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper method to get age
    public int getAge() {
        if (dateNaissance == null) return 0;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }
}