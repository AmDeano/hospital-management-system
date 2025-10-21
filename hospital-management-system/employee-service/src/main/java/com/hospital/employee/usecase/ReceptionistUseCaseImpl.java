package com.hospital.employee.usecase;

import com.hospital.employee.entity.Receptionist;
import com.hospital.employee.repository.ReceptionistRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ReceptionistUseCaseImpl extends AbstractEmployeeUseCase<Receptionist> {

    private final ReceptionistRepository receptionistRepository;
    private final RabbitTemplate rabbitTemplate;
    
    public ReceptionistUseCaseImpl(ReceptionistRepository receptionistRepository, RabbitTemplate rabbitTemplate) {
        super(receptionistRepository, rabbitTemplate);
        this.receptionistRepository = receptionistRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    protected Set<String> determineRoles(Receptionist receptionist) {
        return Set.of("RECEPTIONIST"); // or "HR" depending on your logic
    }
    
    @Override
    protected void updateSpecificFields(Receptionist existing, Receptionist update) {
        if (update.getDeskNumber() != null) existing.setDeskNumber(update.getDeskNumber());
    }

    public Optional<Receptionist> findByDeskNumber(String deskNumber) {
        return receptionistRepository.findByDeskNumber(deskNumber);
    }
}
