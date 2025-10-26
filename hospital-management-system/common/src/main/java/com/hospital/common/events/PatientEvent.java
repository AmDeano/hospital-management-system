package com.hospital.common.events;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class PatientEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;
    private String eventType;
    private String patientId;
    private String patientName;
    private String patientEmail;
    private String patientCin;
    private String dateNaissance; // Added for birth date
    private Boolean isMinor;
    private String parentCin;
    private String numeroTelephone;
    private String adresse;
    private String numeroSecuriteSociale;
    private String eventData;
    private String timestamp;

    // Default constructor for JSON deserialization
    public PatientEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now().toString();
    }

    // Convenience constructor
    public PatientEvent(String eventType, String patientId, String patientName, String patientEmail) {
        this();
        this.eventType = eventType;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
    }

    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PatientEvent event = new PatientEvent();

        public Builder eventType(String eventType) {
            event.eventType = eventType;
            return this;
        }

        public Builder patientId(String patientId) {
            event.patientId = patientId;
            return this;
        }

        public Builder patientName(String patientName) {
            event.patientName = patientName;
            return this;
        }

        public Builder patientEmail(String patientEmail) {
            event.patientEmail = patientEmail;
            return this;
        }

        public Builder patientCin(String patientCin) {
            event.patientCin = patientCin;
            return this;
        }

        public Builder dateNaissance(String dateNaissance) {
            event.dateNaissance = dateNaissance;
            return this;
        }

        public Builder isMinor(Boolean isMinor) {
            event.isMinor = isMinor;
            return this;
        }

        public Builder parentCin(String parentCin) {
            event.parentCin = parentCin;
            return this;
        }

        public Builder numeroTelephone(String numeroTelephone) {
            event.numeroTelephone = numeroTelephone;
            return this;
        }

        public Builder adresse(String adresse) {
            event.adresse = adresse;
            return this;
        }

        public Builder numeroSecuriteSociale(String numeroSecuriteSociale) {
            event.numeroSecuriteSociale = numeroSecuriteSociale;
            return this;
        }

        public Builder eventData(String eventData) {
            event.eventData = eventData;
            return this;
        }

        public PatientEvent build() {
            return event;
        }
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientCin() {
        return patientCin;
    }

    public void setPatientCin(String patientCin) {
        this.patientCin = patientCin;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Boolean getIsMinor() {
        return isMinor;
    }

    public void setIsMinor(Boolean isMinor) {
        this.isMinor = isMinor;
    }

    public String getParentCin() {
        return parentCin;
    }

    public void setParentCin(String parentCin) {
        this.parentCin = parentCin;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
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

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PatientEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", patientId='" + patientId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientEmail='" + patientEmail + '\'' +
                ", patientCin='" + patientCin + '\'' +
                ", dateNaissance='" + dateNaissance + '\'' +
                ", isMinor=" + isMinor +
                ", parentCin='" + parentCin + '\'' +
                ", numeroTelephone='" + numeroTelephone + '\'' +
                ", adresse='" + adresse + '\'' +
                ", numeroSecuriteSociale='" + numeroSecuriteSociale + '\'' +
                ", eventData='" + eventData + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}