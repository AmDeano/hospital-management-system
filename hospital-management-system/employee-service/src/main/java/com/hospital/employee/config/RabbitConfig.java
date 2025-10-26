package com.hospital.employee.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Same queue configuration to listen to patient events
    public static final String PATIENT_QUEUE = "patient.queue";
    public static final String EMPLOYEE_CREATED_QUEUE = "employee.created.queue";
    public static final String USER_CREATED_QUEUE = "user.created.queue";

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public Queue userCreatedQueue() {
    	return new Queue(USER_CREATED_QUEUE, true);
    }
    
    @Bean
    public Queue employeeCreatedQueue() {
        return new Queue("employee.created.queue", true);
    }
}