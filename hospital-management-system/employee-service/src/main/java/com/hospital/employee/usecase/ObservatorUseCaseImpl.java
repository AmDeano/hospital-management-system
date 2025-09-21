package com.hospital.employee.usecase;

import com.hospital.employee.entity.Observator;
import com.hospital.employee.repository.ObservatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ObservatorUseCaseImpl extends AbstractEmployeeUseCase<Observator> {

    private final ObservatorRepository repository;

    public ObservatorUseCaseImpl(ObservatorRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    protected void updateSpecificFields(Observator existing, Observator update) {
        if (update.getAssignedArea() != null) existing.setAssignedArea(update.getAssignedArea());
    }

    public List<Observator> findByAssignedArea(String area) {
        return repository.findByAssignedArea(area);
    }
}
