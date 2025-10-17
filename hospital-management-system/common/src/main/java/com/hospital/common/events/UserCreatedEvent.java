//package com.hospital.common.events;
//
//import java.io.Serializable;
//import java.util.Set;
//
//public class UserCreatedEvent implements Serializable {
//
//    private Long id;
//    private String matricule;
//    private String email;
//    private String externalId;
//    private Set<String> roles;
//
//    // New fields 👇
//    private String firstName;
//    private String lastName;
//
//    public UserCreatedEvent() {}
//
//    public UserCreatedEvent(Long id, String matricule, String email,
//    						String firstName,String lastName,
//                            Set<String> roles, String externalId
//                             ) {
//        this.id = id;
//        this.matricule = matricule;
//        this.email = email;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.roles = roles;
//        this.externalId = externalId;
//    }
//
//	// Getters and Setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getMatricule() { return matricule; }
//    public void setMatricule(String matricule) { this.matricule = matricule; }
//
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//
//    public String getExternalId() { return externalId; }
//    public void setExternalId(String externalId) { this.externalId = externalId; }
//
//    public Set<String> getRoles() { return roles; }
//    public void setRoles(Set<String> roles) { this.roles = roles; }
//
//    public String getFirstName() { return firstName; }
//    public void setFirstName(String firstName) { this.firstName = firstName; }
//
//    public String getLastName() { return lastName; }
//    public void setLastName(String lastName) { this.lastName = lastName; }
//}
