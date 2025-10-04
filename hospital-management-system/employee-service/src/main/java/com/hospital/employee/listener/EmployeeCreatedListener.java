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
        System.out.println("📥 Received EmployeeCreatedEvent: " + event.getMatricule());

        // Get primary role
        String primaryRole = event.getRoles().iterator().next();
        
        try {
            Role role = Role.valueOf(primaryRole);
            
            switch (role) {
                case DOCTOR -> createDoctor(event);
                case NURSE -> createNurse(event);
                case RECEPTIONIST -> createReceptionist(event);
                case ADMIN, HR -> createAdminStaff(event);
                case OBSERVATOR -> createObservator(event);
                default -> System.out.println("⚠️ Unhandled role: " + primaryRole);
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Unknown role: " + primaryRole);
        }
    }

    private void createDoctor(EmployeeCreatedEvent event) {
        if (doctorRepository.existsByMatricule(event.getMatricule())) {
            return;
        }

        Doctor doctor = new Doctor();
        setCommonFields(doctor, event);
        doctor.setRole(Role.DOCTOR);
        // Set default values - will be updated later by employee-service API
        doctor.setSpecialization("General");
        doctor.setLicenseNumber("PENDING");
        doctor.setMedicalDegree("PENDING");
        
        doctorRepository.save(doctor);
        System.out.println("✅ Created Doctor: " + event.getMatricule());
    }

    private void createNurse(EmployeeCreatedEvent event) {
        if (nurseRepository.existsByMatricule(event.getMatricule())) {
            return;
        }

        Nurse nurse = new Nurse();
        setCommonFields(nurse, event);
        nurse.setRole(Role.NURSE);
        nurse.setShift("DAY"); // default
        //nurse.setLicenseNumber("PENDING");
        
        nurseRepository.save(nurse);
        System.out.println("✅ Created Nurse: " + event.getMatricule());
    }

    private void createReceptionist(EmployeeCreatedEvent event) {
        if (receptionistRepository.existsByMatricule(event.getMatricule())) {
            return;
        }

        Receptionist receptionist = new Receptionist();
        setCommonFields(receptionist, event);
        receptionist.setRole(Role.RECEPTIONIST);
        receptionist.setDeskNumber("PENDING");
        
        receptionistRepository.save(receptionist);
        System.out.println("✅ Created Receptionist: " + event.getMatricule());
    }

    private void createAdminStaff(EmployeeCreatedEvent event) {
        if (adminStaffRepository.existsByMatricule(event.getMatricule())) {
            return;
        }

        AdministrativeStaff staff = new AdministrativeStaff();
        setCommonFields(staff, event);
        staff.setRole(event.getRoles().contains("ADMIN") ? Role.ADMIN : Role.HR);
        staff.setDepartmentArea("Administration");
        
        adminStaffRepository.save(staff);
        System.out.println("✅ Created Admin Staff: " + event.getMatricule());
    }

    private void createObservator(EmployeeCreatedEvent event) {
        if (observatorRepository.existsByMatricule(event.getMatricule())) {
            return;
        }

        Observator observator = new Observator();
        setCommonFields(observator, event);
        observator.setRole(Role.OBSERVATOR);
        observator.setAssignedArea("General");
        
        observatorRepository.save(observator);
        System.out.println("✅ Created Observator: " + event.getMatricule());
    }

    private void setCommonFields(Employee employee, EmployeeCreatedEvent event) {
        employee.setMatricule(event.getMatricule());
        employee.setFirstName(event.getFirstName());
        employee.setLastName(event.getLastName());
        employee.setEmail(event.getEmail());
        employee.setIsActive(true);
        employee.setHireDate(java.time.LocalDate.now());
    }
}