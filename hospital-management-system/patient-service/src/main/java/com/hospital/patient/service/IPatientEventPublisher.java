package com.hospital.patient.service;

/**
 * Interface for publishing patient-related events.
 * Supports loose coupling between patient operations and event publishing.
 */
public interface IPatientEventPublisher {

    /**
     * Publish patient created event
     */
    void publishPatientCreated(String patientId, String patientName, String patientEmail,
                              String patientCin, Boolean isMinor, String parentCin);

    /**
     * Publish patient updated event
     */
    void publishPatientUpdated(String patientId, String patientName, String patientEmail,
                              String patientCin, Boolean isMinor, String parentCin, String updateDetails);

    /**
     * Publish patient deleted event
     */
    void publishPatientDeleted(String patientId, String patientName, String patientEmail,
                              String patientCin, Boolean isMinor, String parentCin);

    /**
     * Publish patient status changed event
     */
    void publishPatientStatusChanged(String patientId, String patientName, String patientEmail,
                                    String patientCin, Boolean wasMinor, Boolean isNowMinor);
}
