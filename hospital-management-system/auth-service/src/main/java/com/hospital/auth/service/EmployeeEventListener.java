//package com.hospital.auth.service;
//
//import java.util.stream.Collectors;
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.hospital.auth.entity.Role;
//import com.hospital.auth.entity.UserAccount;
//import com.hospital.auth.repo.UserRepository;
//import com.hospital.common.events.EmployeeCreatedEvent;
//
////In auth-service
//@Service
//public class EmployeeEventListener {
// 
// @Autowired
// private UserRepository userRepository;
// 
// @RabbitListener(queues = "employee.created.queue")
// public void handleEmployeeCreated(EmployeeCreatedEvent event) {
//     if (!userRepository.existsByMatricule(event.getMatricule())) {
//         UserAccount user = new UserAccount();
//         user.setMatricule(event.getMatricule());
//         user.setEmail(event.getEmail());
//         user.setFirstName(event.getFirstName());
//         user.setLastName(event.getLastName());
//         user.setPasswordHash(event.getPasswordHash()); // Already hashed
//         user.setEnabled(true);
//         user.setExternalId(event.getExternalId());
//         // Convert string roles to Role enum
//         user.setRoles(event.getRoles().stream()
//             .map(Role::valueOf)
//             .collect(Collectors.toSet()));
//         
//         userRepository.save(user);
//     }
// }
//}
