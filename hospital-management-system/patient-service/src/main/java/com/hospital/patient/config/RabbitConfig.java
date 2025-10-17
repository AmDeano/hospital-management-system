package com.hospital.patient.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PATIENT_QUEUE = "patient.events.queue";
    public static final String EXCHANGE_NAME = "hospital.exchange";
    public static final String PATIENT_ROUTING_KEY = "patient.events";
    
    @Bean
    public Queue patientQueue() {
        return QueueBuilder.durable(PATIENT_QUEUE).build();
    }

    @Bean
    public DirectExchange patientExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding patientBinding() {
        return BindingBuilder
                .bind(patientQueue())
                .to(patientExchange())
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
    public DirectExchange hospitalExchange() {
        return new DirectExchange("hospital.exchange");
    }
}



    
