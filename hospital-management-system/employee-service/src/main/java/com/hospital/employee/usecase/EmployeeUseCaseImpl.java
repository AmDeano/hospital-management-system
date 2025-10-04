package com.hospital.employee.usecase;

import com.hospital.employee.entity.Employee;
import com.hospital.employee.repository.EmployeeRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeUseCaseImpl extends AbstractEmployeeUseCase<Employee> {

    public EmployeeUseCaseImpl(EmployeeRepository repository, RabbitTemplate rabbitTemplate) {
        super(repository, rabbitTemplate);
    }
}
