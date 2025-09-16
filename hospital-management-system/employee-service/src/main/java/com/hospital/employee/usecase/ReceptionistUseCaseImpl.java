package com.hospital.employee.usecase;

import com.hospital.employee.entity.Receptionist;
import com.hospital.employee.exception.EmployeeNotFoundException;
import com.hospital.employee.repository.ReceptionistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ReceptionistUseCaseImpl implements EmployeeUseCase<Receptionist> {

    private final ReceptionistRepository receptionistRepository;

    public ReceptionistUseCaseImpl(ReceptionistRepository receptionistRepository) {
        this.receptionistRepository = receptionistRepository;
    }

    @Override
    public Receptionist create(Receptionist receptionist) {
        return receptionistRepository.save(receptionist);
    }

    @Override
    public Receptionist update(Long id, Receptionist receptionist) {
        Receptionist existing = findById(id);
        
        if (receptionist.getFirstName() != null) {
            existing.setFirstName(receptionist.getFirstName());
        }
        if (receptionist.getLastName() != null) {
            existing.setLastName(receptionist.getLastName());
        }
        if (receptionist.getEmail() != null) {
            existing.setEmail(receptionist.getEmail());
        }
        if (receptionist.getPhone() != null) {
            existing.setPhone(receptionist.getPhone());
        }
        if (receptionist.getDeskNumber() != null) {
            existing.setDeskNumber(receptionist.getDeskNumber());
        }
        if (receptionist.getDepartment() != null) {
            existing.setDepartment(receptionist.getDepartment());
        }
        if (receptionist.getAddress() != null) {
            existing.setAddress(receptionist.getAddress());
        }
        if (receptionist.getHireDate() != null) {
            existing.setHireDate(receptionist.getHireDate());
        }
        if (receptionist.getIsActive() != null) {
            existing.setIsActive(receptionist.getIsActive());
        }
        
        return receptionistRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        receptionistRepository.deleteById(id);
    }

    @Override
    public Receptionist findById(Long id) {
        return receptionistRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Receptionist not found with id: " + id));
    }

    @Override
    public List<Receptionist> findAll() {
        return receptionistRepository.findAll();
    }
}