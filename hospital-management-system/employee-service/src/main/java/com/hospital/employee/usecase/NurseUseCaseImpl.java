package com.hospital.employee.usecase;

import com.hospital.employee.config.RabbitConfig;
import com.hospital.employee.entity.Nurse;
import com.hospital.employee.repository.NurseRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class NurseUseCaseImpl extends AbstractEmployeeUseCase<Nurse> {

    private final RabbitConfig rabbitConfig;

    private final NurseRepository nurseRepository;
    private final RabbitTemplate rabbitTemplate;
    
    public NurseUseCaseImpl(NurseRepository nurseRepository,RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig) {
        super(nurseRepository, rabbitTemplate);
		this.rabbitConfig = rabbitConfig;
        this.nurseRepository = nurseRepository;
        this.rabbitTemplate = rabbitTemplate;
    }
    @Override
    protected Set<String> determineRoles(Nurse nurse) {
        return Set.of("NURSE");
    }
    
    @Override
    protected void updateSpecificFields(Nurse existing, Nurse update) {
        if (update.getShift() != null) existing.setShift(update.getShift());
        if (update.getNursingLicense() != null) existing.setNursingLicense(update.getNursingLicense());
    }

    public List<Nurse> findByShift(String shift) {
        return nurseRepository.findByShift(shift);
    }

    public List<Nurse> findActiveByShift(String shift) {
        return nurseRepository.findActiveNursesByShift(shift);
    }
}
