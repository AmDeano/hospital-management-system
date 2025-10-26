//package com.hospital.employee.listener;
//
//import com.hospital.common.events.UserCreatedEvent;
//import com.hospital.employee.config.RabbitConfig;
//import com.hospital.employee.entity.AdministrativeStaff;
//import com.hospital.employee.entity.Doctor;
//import com.hospital.employee.entity.Employee;
//import com.hospital.employee.entity.Nurse;
//import com.hospital.employee.entity.Role;
//import com.hospital.employee.repository.EmployeeRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UserCreatedListener {
//
//    private static final Logger log = LoggerFactory.getLogger(UserCreatedListener.class);
//
//    private final EmployeeRepository employeeRepository;
//
//    public UserCreatedListener(EmployeeRepository employeeRepository) {
//        this.employeeRepository = employeeRepository;
//    }
//
//    @RabbitListener(queues = RabbitConfig.USER_CREATED_QUEUE)
//    public void handleUserCreated(UserCreatedEvent event) {
//        log.info("📩 Received UserCreatedEvent: {} {}", event.getFirstName(), event.getLastName());
//
//        Employee employee;
//
//        // Example mapping based on role
//        String role = event.getRoles().iterator().next();
//        switch (role) {
//            case "DOCTOR" -> {
//                Doctor doctor = new Doctor();
//                doctor.setSpecialization("General"); // default
//                doctor.setLicenseNumber("TEMP-" + event.getExternalId());
//                doctor.setMedicalDegree("MD");
//                employee = doctor;
//            }
//            case "NURSE" -> {
//                Nurse nurse = new Nurse();
//                nurse.setShift("DAY");
//                nurse.setNursingLicense("TEMP-" + event.getExternalId());
//                employee = nurse;
//            }
//            default -> {
//                AdministrativeStaff admin = new AdministrativeStaff();
//                admin.setDepartmentArea("General");
//                employee = admin;
//            }
//        }
//
//        // Shared fields
//        employee.setMatricule(event.getExternalId());
//        employee.setFirstName(event.getFirstName());
//        employee.setLastName(event.getLastName());
//        employee.setEmail(event.getEmail());
//        employee.setRole(Role.valueOf(role));
//        employee.setIsActive(true);
//
//        employeeRepository.save(employee);
//        log.info("✅ Employee record created for {} {}", event.getFirstName(), event.getLastName());
//    }
//
//}
