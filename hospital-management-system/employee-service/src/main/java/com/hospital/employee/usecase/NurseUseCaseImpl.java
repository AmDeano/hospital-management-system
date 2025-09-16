package com.hospital.employee.usecase;

import com.hospital.employee.entity.Nurse;
import com.hospital.employee.exception.EmployeeNotFoundException;
import com.hospital.employee.repository.NurseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class NurseUseCaseImpl implements EmployeeUseCase<Nurse> {

    private final NurseRepository nurseRepository;

    public NurseUseCaseImpl(NurseRepository nurseRepository) {
        this.nurseRepository = nurseRepository;
    }

    @Override
    public Nurse create(Nurse nurse) {
        return nurseRepository.save(nurse);
    }

    @Override
    public Nurse update(Long id, Nurse nurse) {
        Nurse existing = findById(id);
        
        if (nurse.getFirstName() != null) {
            existing.setFirstName(nurse.getFirstName());
        }
        if (nurse.getLastName() != null) {
            existing.setLastName(nurse.getLastName());
        }
        if (nurse.getEmail() != null) {
            existing.setEmail(nurse.getEmail());
        }
        if (nurse.getPhone() != null) {
            existing.setPhone(nurse.getPhone());
        }
        if (nurse.getShift() != null) {
            existing.setShift(nurse.getShift());
        }
        if (nurse.getNursingLicense() != null) {
            existing.setNursingLicense(nurse.getNursingLicense());
        }
        if (nurse.getDepartment() != null) {
            existing.setDepartment(nurse.getDepartment());
        }
        if (nurse.getAddress() != null) {
            existing.setAddress(nurse.getAddress());
        }
        if (nurse.getHireDate() != null) {
            existing.setHireDate(nurse.getHireDate());
        }
        if (nurse.getIsActive() != null) {
            existing.setIsActive(nurse.getIsActive());
        }
        
        return nurseRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        nurseRepository.deleteById(id);
    }

    @Override
    public Nurse findById(Long id) {
        return nurseRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Nurse not found with id: " + id));
    }

    @Override
    public List<Nurse> findAll() {
        return nurseRepository.findAll();
    }
}