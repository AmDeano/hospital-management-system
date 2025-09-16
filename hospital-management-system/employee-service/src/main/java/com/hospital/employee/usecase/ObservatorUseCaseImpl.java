package com.hospital.employee.usecase;

import com.hospital.employee.entity.Observator;
import com.hospital.employee.exception.EmployeeNotFoundException;
import com.hospital.employee.repository.ObservatorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ObservatorUseCaseImpl implements EmployeeUseCase<Observator> {

    private final ObservatorRepository observatorRepository;

    public ObservatorUseCaseImpl(ObservatorRepository observatorRepository) {
        this.observatorRepository = observatorRepository;
    }

    @Override
    public Observator create(Observator observator) {
        return observatorRepository.save(observator);
    }

    @Override
    public Observator update(Long id, Observator observator) {
        Observator existing = findById(id);
        existing.setFirstName(observator.getFirstName());
        existing.setLastName(observator.getLastName());
        existing.setEmail(observator.getEmail());
        existing.setPhone(observator.getPhone());
        existing.setAssignedArea(observator.getAssignedArea());
        existing.setDepartment(observator.getDepartment());
        existing.setAddress(observator.getAddress());
        existing.setHireDate(observator.getHireDate());
        existing.setIsActive(observator.getIsActive());
        return observatorRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        observatorRepository.deleteById(id);
    }

    @Override
    public Observator findById(Long id) {
        return observatorRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Observator not found with id: " + id));
    }

    @Override
    public List<Observator> findAll() {
        return observatorRepository.findAll();
    }
}
