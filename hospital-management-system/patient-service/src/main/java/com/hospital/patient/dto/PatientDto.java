package com.hospital.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientDto {
    
    private String id;
    
    @NotBlank(message = "Name is required")
    private String nom;
    
    @Past(message = "Birth date must be in the past")
    @NotNull(message = "Birth date is required")
    private LocalDate dateNaissance;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String numeroTelephone;
    private String adresse;
    private String numeroSecuriteSociale;
    private String cin; // CIN for adults
    private Boolean isMinor;
    private String parentCin; // CIN of parent/guardian for minors
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public PatientDto() {}
    
    public PatientDto(String id, String nom, LocalDate dateNaissance, String email, 
                      String numeroTelephone, String adresse, String numeroSecuriteSociale,
                      String cin, Boolean isMinor, String parentCin,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.numeroTelephone = numeroTelephone;
        this.adresse = adresse;
        this.numeroSecuriteSociale = numeroSecuriteSociale;
        this.cin = cin;
        this.isMinor = isMinor;
        this.parentCin = parentCin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    
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
}