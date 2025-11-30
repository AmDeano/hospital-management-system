package com.hospital.employee.service;

import com.hospital.employee.entity.Observator;
import com.hospital.employee.repository.ObservatorRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ObservatorService extends AbstractEmployeeService<Observator> {

    private final ObservatorRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public ObservatorService(ObservatorRepository repository, RabbitTemplate rabbitTemplate) {
        super(repository, rabbitTemplate);
        this.repository = repository;
		this.rabbitTemplate = rabbitTemplate;
    }
    @Override
    protected Set<String> determineRoles(Observator observator) {
        return Set.of("OBSERVATOR"); // or "HR" depending on your logic
    }
    
    @Override
    protected void updateSpecificFields(Observator existing, Observator update) {
        if (update.getAssignedArea() != null) existing.setAssignedArea(update.getAssignedArea());
    }

    public List<Observator> findByAssignedArea(String area) {
        return repository.findByAssignedArea(area);
    }
}
