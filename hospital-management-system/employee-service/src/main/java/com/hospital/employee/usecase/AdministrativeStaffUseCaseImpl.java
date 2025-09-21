package com.hospital.employee.usecase;

import com.hospital.employee.entity.AdministrativeStaff;
import com.hospital.employee.repository.AdministrativeStaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdministrativeStaffUseCaseImpl extends AbstractEmployeeUseCase<AdministrativeStaff> {

    private final AdministrativeStaffRepository repository;

    public AdministrativeStaffUseCaseImpl(AdministrativeStaffRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    protected void updateSpecificFields(AdministrativeStaff existing, AdministrativeStaff update) {
        if (update.getDepartment() != null) existing.setDepartment(update.getDepartment());
    }

    public List<AdministrativeStaff> findByDepartmentArea(String area) {
        return repository.findByDepartmentArea(area);
    }
}
