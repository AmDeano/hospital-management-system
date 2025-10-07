package com.hospital.employee.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.hospital.common.events.EmployeeCreatedEvent;
import com.hospital.employee.entity.*;
import com.hospital.employee.repository.*;

@Component
public class EmployeeCreatedListener {

    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final ReceptionistRepository receptionistRepository;
    private final AdministrativeStaffRepository adminStaffRepository;
    private final ObservatorRepository observatorRepository;

    public EmployeeCreatedListener(
            DoctorRepository doctorRepository,
            NurseRepository nurseRepository,
            ReceptionistRepository receptionistRepository,
            AdministrativeStaffRepository adminStaffRepository,
            ObservatorRepository observatorRepository) {
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.receptionistRepository = receptionistRepository;
        this.adminStaffRepository = adminStaffRepository;
        this.observatorRepository = observatorRepository;
    }

    @RabbitListener(queues = "employee.created.queue")
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        System.out.println("Received EmployeeCreatedEvent: " + event.getMatricule());

        String primaryRole = event.getRoles().iterator().next();
        
        try {
            Role role = Role.valueOf(primaryRole);
            
            switch (role) {
                case DOCTOR -> createDoctor(event);
                case NURSE -> createNurse(event);
                case RECEPTIONIST -> createReceptionist(event);
                case ADMIN, HR -> createAdminStaff(event);
                case OBSERVATOR -> createObservator(event);
                default -> System.out.println("Unhandled role: " + primaryRole);
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown role: " + primaryRole);
        }
    }

    private void createDoctor(EmployeeCreatedEvent event) {
        if (doctorRepository.existsByMatricule(event.getMatricule())) {
            return;
        }

        Doctor doctor = new Doctor();
        setCommonFields(doctor, event);
        doctor.setRole(Role.DOCTOR);
        
        // Set doctor-specific fields with validation
        doctor.setSpecialization(
            event.getSpecialization() != null && !event.getSpecialization().isBlank() 
                ? event.getSpecialization() 
                : "General"
        );
        doctor.setLicenseNumber(
            event.getLicenseNumber() != null && !event.getLicenseNumber().isBlank()
                ? event.getLicenseNumber()
                : "PENDING"
        );
        doctor.setMedicalDegree(
            event.getMedicalDegree() != null && !event.getMedicalDegree().isBlank()
                ? event.getMedicalDegree()
                : "PENDING"
        );
        
        doctorRepository.save(doctor);
        System.out.println("Created Doctor: " + event.getMatricule());
    }

    private void createNurse(EmployeeCreatedEvent event) {
        if (nurseRepository.existsByMatricule(event.getMatricule())) {
            return;
        }

        Nurse nurse = new Nurse();
        setCommonFields(nurse, event);
        nurse.setRole(Role.NURSE);
        
        nurse.setShift(
            event.getShift() != null && !event.getShift().isBlank()
                ? event.getShift()
                : "DAY"
        );
        nurse.setNursingLicense(
            event.getNurseLicenseNumber() != null && !event.getNurseLicenseNumber().isBlank()
                ? event.getNurseLicenseNumber()
                : "PENDING"
        );
        
        nurseRepository.save(nurse);
        System.out.println("Created Nurse: " + event.getMatricule());
    }

    private void createReceptionist(EmployeeCreatedEvent event) {
        if (receptionistRepository.existsByMatricule(event.getMatricule())) {
            return;
        }

        Receptionist receptionist = new Receptionist();
        setCommonFields(receptionist, event);
        receptionist.setRole(Role.RECEPTIONIST);
        
        receptionist.setDeskNumber(
            event.getDeskNumber() != null && !event.getDeskNumber().isBlank()
                ? event.getDeskNumber()
                : "PENDING"
        );
        
        receptionistRepository.save(receptionist);
        System.out.println("Created Receptionist: " + event.getMatricule());
    }

    private void createAdminStaff(EmployeeCreatedEvent event) {
        if (adminStaffRepository.existsByMatricule(event.getMatricule())) {
            return;
        }

        AdministrativeStaff staff = new AdministrativeStaff();
        setCommonFields(staff, event);
        staff.setRole(event.getRoles().contains("ADMIN") ? Role.ADMIN : Role.HR);
        
        staff.setDepartmentArea(
            event.getDepartmentArea() != null && !event.getDepartmentArea().isBlank()
                ? event.getDepartmentArea()
                : "Administration"
        );
        
        adminStaffRepository.save(staff);
        System.out.println("Created Admin Staff: " + event.getMatricule());
    }

    private void createObservator(EmployeeCreatedEvent event) {
        if (observatorRepository.existsByMatricule(event.getMatricule())) {
            return;
        }

        Observator observator = new Observator();
        setCommonFields(observator, event);
        observator.setRole(Role.OBSERVATOR);
        
        observator.setAssignedArea(
            event.getAssignedArea() != null && !event.getAssignedArea().isBlank()
                ? event.getAssignedArea()
                : "General"
        );
        
        observatorRepository.save(observator);
        System.out.println("Created Observator: " + event.getMatricule());
    }

    private void setCommonFields(Employee employee, EmployeeCreatedEvent event) {
        employee.setMatricule(event.getMatricule());
        employee.setFirstName(event.getFirstName());
        employee.setLastName(event.getLastName());
        employee.setEmail(event.getEmail());
        employee.setPhone(event.getPhone());
        employee.setAddress(event.getAddress());
        employee.setIsActive(true);
        employee.setHireDate(java.time.LocalDate.now());
    }
}