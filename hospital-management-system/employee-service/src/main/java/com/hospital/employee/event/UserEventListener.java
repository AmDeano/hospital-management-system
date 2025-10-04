package com.hospital.employee.event;

import com.hospital.common.events.UserCreatedEvent;
import com.hospital.employee.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventListener {

    /**
     * Listen for user.created events from auth-service.
     * This method will automatically receive messages converted
     * into UserCreatedEvent objects (thanks to Jackson2JsonMessageConverter).
     */
    @RabbitListener(queues = RabbitConfig.USER_CREATED_QUEUE)
    public void handleUserCreated(UserCreatedEvent event) {
        System.out.println("📩 Received user event: " 
            + event.getMatricule() 
            + " with role(s): " 
            + event.getRoles());

        // TODO: Map this UserCreatedEvent to Employee entity if needed
        // Example:
        // Employee employee = new Employee();
        // employee.setExternalId(event.getExternalId());
        // employee.setUsername(event.getUsername());
        // employee.setEmail(event.getEmail());
        // employee.setRoles(event.getRoles());
        // employeeRepository.save(employee);
    }
}
