package com.hospital.employee.usecase;

import com.hospital.employee.entity.AdministrativeStaff;
import com.hospital.employee.repository.AdministrativeStaffRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AdministrativeStaffUseCaseImpl extends AbstractEmployeeUseCase<AdministrativeStaff> {

    private final AdministrativeStaffRepository repository;
    private final RabbitTemplate rabbitTemplate;
    
    public AdministrativeStaffUseCaseImpl(AdministrativeStaffRepository repository, RabbitTemplate rabbitTemplate) {
        super(repository, rabbitTemplate);
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }
    @Override
    protected Set<String> determineRoles(AdministrativeStaff staff) {
        return Set.of("ADMIN"); // or "HR" depending on your logic
    }

    @Override
    protected void updateSpecificFields(AdministrativeStaff existing, AdministrativeStaff update) {
        if (update.getDepartment() != null) existing.setDepartment(update.getDepartment());
    }

    public List<AdministrativeStaff> findByDepartmentArea(String area) {
        return repository.findByDepartmentArea(area);
    }
}
