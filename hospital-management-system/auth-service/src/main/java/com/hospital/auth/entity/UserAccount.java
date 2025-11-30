// auth-service/src/main/java/com/hospital/auth/entity/UserAccount.java
package com.hospital.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String matricule;

  @Column(nullable = false)
  private String passwordHash;

  @Column(unique = true)
  private String email;
  
  // New fields 👇
  private String firstName;
  
  private String lastName;

  private String externalId;
  
  private String CIN;

  private LocalDate dateNaissance;
  private String numeroTelephone;
  private String numeroSecuriteSociale;
  private String adresse;
  private String parentCin;

  
  

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Set<Role> roles;

  private boolean enabled = true;

  private Instant createdAt = Instant.now();

    // ===== Getters and Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
	public String getCIN() { return CIN; }
	public void setCIN(String CIN) { this.CIN = CIN; }

	public LocalDate getDateNaissance() { return dateNaissance; }
	public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

	public String getNumeroSecuriteSociale() { return numeroSecuriteSociale; }
	public void setNumeroSecuriteSociale(String numeroSecuriteSociale) { this.numeroSecuriteSociale = numeroSecuriteSociale; }

	public String getAdresse() { return adresse; }
	public void setAdresse(String adresse) { this.adresse = adresse; }

	public String getParentCin() { return parentCin; }
	public void setParentCin(String parentCin) { this.parentCin = parentCin; }

	public String getNumeroTelephone() { return numeroTelephone; }
	public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }
}
