package com.hospital.auth.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Queue name must match employee-service listener
    public static final String USER_CREATED_QUEUE = "user.created.queue";
    public static final String EMPLOYEE_CREATED_QUEUE = "employee.created.queue";
    
    @Bean
    public Queue employeeCreatedQueue() {
        return new Queue(EMPLOYEE_CREATED_QUEUE, true);
    }
    
    @Bean
    public Queue userCreatedQueue() {
        // durable = true means it survives broker restarts
        return new Queue(USER_CREATED_QUEUE, true);
    }

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
}
