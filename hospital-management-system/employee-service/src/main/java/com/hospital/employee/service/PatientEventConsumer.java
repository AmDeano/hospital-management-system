package com.hospital.employee.service;

import com.hospital.common.events.PatientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PatientEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PatientEventConsumer.class);

    @RabbitListener(queues = "patient.queue")
    public void handlePatientEvent(PatientEvent event) {
        logger.info("Received patient event: {} for patient ID: {}", event.getEventType(), event.getPatientId());
        
        try {
            switch (event.getEventType()) {
                case "PATIENT_CREATED":
                    handlePatientCreated(event);
                    break;
                case "PATIENT_UPDATED":
                    handlePatientUpdated(event);
                    break;
                case "PATIENT_DELETED":
                    handlePatientDeleted(event);
                    break;
                case "PATIENT_STATUS_CHANGED":
                    handlePatientStatusChanged(event);
                    break;
                default:
                    logger.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            logger.error("Error processing patient event: {}", event, e);
            // In production, you might want to send to dead letter queue or retry mechanism
        }
    }

    private void handlePatientCreated(PatientEvent event) {
        logger.info("Processing patient created - ID: {}, Name: {}, isMinor: {}", 
                   event.getPatientId(), event.getPatientName(), event.getIsMinor());
        
        // Business logic examples:
        // 1. Create initial medical record
        // 2. Send welcome email/SMS
        // 3. Assign to default doctor if minor
        // 4. Update hospital statistics
        // 5. Create billing account
        // 6. Generate patient card
        
        try {
            if (event.getIsMinor() != null && event.getIsMinor()) {
                handleMinorPatientCreated(event);
            } else {
                handleAdultPatientCreated(event);
            }
            
            // Send notification to relevant staff
            notifyStaffOfNewPatient(event);
            
            // Update dashboard statistics
            updatePatientStatistics("CREATED", event.getIsMinor());
            
        } catch (Exception e) {
            logger.error("Error in patient created handling", e);
        }
    }

    private void handlePatientUpdated(PatientEvent event) {
        logger.info("Processing patient updated - ID: {}, Name: {}, Changes: {}", 
                   event.getPatientId(), event.getPatientName(), event.getEventData());
        
        // Business logic examples:
        // 1. Update related medical records
        // 2. Notify assigned doctors of changes
        // 3. Update appointment systems
        // 4. Refresh patient cards if needed
        // 5. Audit trail logging
        
        try {
            // Log the update for audit purposes
            logPatientUpdate(event);
            
            // If contact info changed, update communication preferences
            if (event.getEventData().contains("Email changed") || 
                event.getEventData().contains("Name changed")) {
                updatePatientCommunications(event);
            }
            
            // Notify staff of significant changes
            if (isSignificantUpdate(event.getEventData())) {
                notifyStaffOfPatientUpdate(event);
            }
            
        } catch (Exception e) {
            logger.error("Error in patient updated handling", e);
        }
    }

    private void handlePatientDeleted(PatientEvent event) {
        logger.info("Processing patient deleted - ID: {}, Name: {}", 
                   event.getPatientId(), event.getPatientName());
        
        // Business logic examples:
        // 1. Archive medical records (don't delete, keep for legal reasons)
        // 2. Cancel future appointments
        // 3. Close billing accounts
        // 4. Notify assigned medical staff
        // 5. Update statistics
        // 6. Remove from active patient lists
        
        try {
            // Archive patient data instead of deleting
            archivePatientData(event);
            
            // Cancel future appointments
            cancelFutureAppointments(event.getPatientId());
            
            // Notify assigned staff
            notifyStaffOfPatientDeletion(event);
            
            // Update statistics
            updatePatientStatistics("DELETED", event.getIsMinor());
            
        } catch (Exception e) {
            logger.error("Error in patient deleted handling", e);
        }
    }

    private void handlePatientStatusChanged(PatientEvent event) {
        logger.info("Processing patient status change - ID: {}, Name: {}, Change: {}", 
                   event.getPatientId(), event.getPatientName(), event.getEventData());
        
        // Business logic examples:
        // 1. Update patient ID systems (from MIN-XXXX to CIN)
        // 2. Transfer from pediatric to adult care
        // 3. Update legal guardian requirements
        // 4. Modify billing and insurance settings
        // 5. Update access permissions
        
        try {
            // Update internal patient tracking systems
            updatePatientIdInSystems(event);
            
            // Handle care transfer if moving from minor to adult
            if (event.getEventData().contains("minor to adult")) {
                handleMinorToAdultTransfer(event);
            }
            
        } catch (Exception e) {
            logger.error("Error in patient status change handling", e);
        }
    }

    private void handleMinorPatientCreated(PatientEvent event) {
        logger.info("Setting up minor patient: {} with parent CIN: {}", 
                   event.getPatientId(), event.getParentCin());
        
        // Minor-specific setup
        // 1. Assign to pediatric department
        // 2. Link to parent's account
        // 3. Set up parental consent requirements
        // 4. Configure child-safe communication preferences
        
        assignToPediatricDepartment(event.getPatientId());
        linkToParentAccount(event.getPatientId(), event.getParentCin());
        setupParentalConsentRequirements(event.getPatientId());
    }

    private void handleAdultPatientCreated(PatientEvent event) {
        logger.info("Setting up adult patient: {} with CIN: {}", 
                   event.getPatientId(), event.getPatientCin());
        
        // Adult-specific setup
        // 1. Assign to general or specialized department
        // 2. Set up direct communication
        // 3. Enable full account access
        // 4. Configure insurance and billing
        
        assignToGeneralDepartment(event.getPatientId());
        setupDirectCommunication(event.getPatientId(), event.getPatientEmail());
        enableFullAccountAccess(event.getPatientId());
    }

    private void notifyStaffOfNewPatient(PatientEvent event) {
        logger.info("Notifying staff of new patient: {}", event.getPatientName());
        
        // Implementation examples:
        // 1. Send email to registration desk
        // 2. Update duty nurse dashboard
        // 3. Add to doctor's patient list if pre-assigned
        // 4. Update hospital capacity metrics
        
        // Simulate staff notification
        String notificationMessage = String.format(
            "New patient registered: %s (ID: %s) - %s", 
            event.getPatientName(), 
            event.getPatientId(),
            event.getIsMinor() ? "Minor" : "Adult"
        );
        
        // In real implementation, this would send actual notifications
        logger.info("STAFF NOTIFICATION: {}", notificationMessage);
    }

    private void updatePatientStatistics(String operation, Boolean isMinor) {
        logger.info("Updating patient statistics: {} - {}", operation, isMinor ? "Minor" : "Adult");
        
        // Implementation examples:
        // 1. Increment/decrement total patient count
        // 2. Update minor vs adult ratios
        // 3. Update department capacity
        // 4. Trigger dashboard refresh
        
        // Simulate statistics update
        String statsUpdate = String.format(
            "Statistics updated: %s %s patient", 
            operation, 
            isMinor ? "minor" : "adult"
        );
        logger.info("STATS UPDATE: {}", statsUpdate);
    }

    private void logPatientUpdate(PatientEvent event) {
        logger.info("Logging patient update for audit: ID={}, Changes={}", 
                   event.getPatientId(), event.getEventData());
        
        // Implementation examples:
        // 1. Write to audit log database
        // 2. Create versioned backup of patient data
        // 3. Track who made changes (from event context)
        // 4. Maintain compliance records
        
        // Simulate audit logging
        logger.info("AUDIT LOG: Patient {} updated at {} - {}", 
                   event.getPatientId(), event.getTimestamp(), event.getEventData());
    }

    private void updatePatientCommunications(PatientEvent event) {
        logger.info("Updating communication preferences for patient: {}", event.getPatientId());
        
        // Implementation examples:
        // 1. Update email distribution lists
        // 2. Refresh SMS notification services
        // 3. Update emergency contact systems
        // 4. Sync with appointment reminder systems
        
        logger.info("COMMUNICATION UPDATE: Updated contact info for patient {}", event.getPatientId());
    }

    private boolean isSignificantUpdate(String eventData) {
        // Define what constitutes a significant update that requires staff notification
        return eventData.contains("Email changed") || 
               eventData.contains("Name changed") || 
               eventData.contains("CIN changed") || 
               eventData.contains("Age status changed");
    }

    private void notifyStaffOfPatientUpdate(PatientEvent event) {
        logger.info("Notifying staff of significant patient update: {}", event.getPatientId());
        
        String notificationMessage = String.format(
            "IMPORTANT: Patient %s (ID: %s) has been updated. Changes: %s", 
            event.getPatientName(), 
            event.getPatientId(), 
            event.getEventData()
        );
        
        logger.info("STAFF NOTIFICATION: {}", notificationMessage);
    }

    private void archivePatientData(PatientEvent event) {
        logger.info("Archiving data for deleted patient: {}", event.getPatientId());
        
        // Implementation examples:
        // 1. Move patient data to archive database
        // 2. Create compressed backup files
        // 3. Update data retention schedules
        // 4. Maintain legal compliance records
        
        logger.info("ARCHIVE: Patient {} data archived successfully", event.getPatientId());
    }

    private void cancelFutureAppointments(String patientId) {
        logger.info("Cancelling future appointments for patient: {}", patientId);
        
        // Implementation examples:
        // 1. Query appointment system for future appointments
        // 2. Send cancellation notifications to doctors
        // 3. Update scheduling system availability
        // 4. Process any necessary refunds
        
        logger.info("APPOINTMENTS: All future appointments cancelled for patient {}", patientId);
    }

    private void notifyStaffOfPatientDeletion(PatientEvent event) {
        logger.info("Notifying staff of patient deletion: {}", event.getPatientId());
        
        String notificationMessage = String.format(
            "ALERT: Patient %s (ID: %s) has been removed from the system", 
            event.getPatientName(), 
            event.getPatientId()
        );
        
        logger.info("STAFF NOTIFICATION: {}", notificationMessage);
    }

    private void updatePatientIdInSystems(PatientEvent event) {
        logger.info("Updating patient ID across systems: {}", event.getPatientId());
        
        // Implementation examples:
        // 1. Update appointment system patient IDs
        // 2. Migrate medical record references
        // 3. Update billing system identifiers
        // 4. Refresh access control systems
        
        logger.info("SYSTEM UPDATE: Patient ID updated across all systems for {}", event.getPatientId());
    }

    private void handleMinorToAdultTransfer(PatientEvent event) {
        logger.info("Handling minor to adult transfer for patient: {}", event.getPatientId());
        
        // Implementation examples:
        // 1. Transfer from pediatric to adult care
        // 2. Update legal guardian requirements
        // 3. Change consent and privacy settings
        // 4. Update insurance and billing configurations
        // 5. Modify communication preferences
        
        logger.info("CARE TRANSFER: Patient {} transferred from pediatric to adult care", event.getPatientId());
        
        // Remove parental access controls
        removeParentalAccessControls(event.getPatientId());
        
        // Grant full adult privileges
        grantAdultPrivileges(event.getPatientId());
        
        // Notify care transition team
        notifyCareTransitionTeam(event);
    }

    // Helper methods for specific business logic
    
    private void assignToPediatricDepartment(String patientId) {
        logger.info("Assigning patient {} to pediatric department", patientId);
        // Implementation: Update department assignment in internal systems
    }

    private void linkToParentAccount(String patientId, String parentCin) {
        logger.info("Linking patient {} to parent account {}", patientId, parentCin);
        // Implementation: Create parent-child relationship in database
    }

    private void setupParentalConsentRequirements(String patientId) {
        logger.info("Setting up parental consent requirements for patient {}", patientId);
        // Implementation: Configure consent workflow for medical procedures
    }

    private void assignToGeneralDepartment(String patientId) {
        logger.info("Assigning patient {} to general department", patientId);
        // Implementation: Update department assignment for adult patients
    }

    private void setupDirectCommunication(String patientId, String email) {
        logger.info("Setting up direct communication for patient {} at {}", patientId, email);
        // Implementation: Configure direct email/SMS communications
    }

    private void enableFullAccountAccess(String patientId) {
        logger.info("Enabling full account access for patient {}", patientId);
        // Implementation: Grant full access to patient portal and records
    }

    private void removeParentalAccessControls(String patientId) {
        logger.info("Removing parental access controls for patient {}", patientId);
        // Implementation: Revoke parent access to patient records
    }

    private void grantAdultPrivileges(String patientId) {
        logger.info("Granting adult privileges to patient {}", patientId);
        // Implementation: Enable all adult patient features
    }

    private void notifyCareTransitionTeam(PatientEvent event) {
        logger.info("Notifying care transition team of patient status change: {}", event.getPatientId());
        
        String notificationMessage = String.format(
            "CARE TRANSITION: Patient %s (ID: %s) requires care transition review. %s", 
            event.getPatientName(), 
            event.getPatientId(), 
            event.getEventData()
        );
        
        logger.info("CARE TEAM NOTIFICATION: {}", notificationMessage);
        // Implementation: Send notification to care transition specialists
    }
}