package com.hospital.common.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class PatientEvent {
    private String eventId;
    private String eventType;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String eventData;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    // Default constructor for JSON
    public PatientEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public PatientEvent(String eventType, Long patientId, String patientName, String patientEmail) {
        this();
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }
    
    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "PatientEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", patientId=" + patientId +
                ", patientName='" + patientName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}