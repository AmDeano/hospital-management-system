package com.hospital.auth.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PATIENT_QUEUE = "patient.events.queue";
    public static final String PATIENT_EXCHANGE = "hospital.exchange";
    public static final String PATIENT_ROUTING_KEY = "patient.events";
    public static final String EMPLOYEE_CREATED_QUEUE = "employee.created.queue";

    @Bean
    public Queue patientQueue() {
        return QueueBuilder.durable(PATIENT_QUEUE).build();
    }

    @Bean
    public DirectExchange hospitalExchange() {
        return new DirectExchange(PATIENT_EXCHANGE);
    }

    @Bean
    public Binding patientBinding() {
        return BindingBuilder.bind(patientQueue())
                .to(hospitalExchange())
                .with(PATIENT_ROUTING_KEY);
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
    
    @Bean
    public Queue employeeCreatedQueue() {
        return new Queue(EMPLOYEE_CREATED_QUEUE, true);
    }

}
