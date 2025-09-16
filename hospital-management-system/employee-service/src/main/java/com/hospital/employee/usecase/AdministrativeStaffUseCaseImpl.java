package com.hospital.employee.usecase;

import com.hospital.employee.entity.AdministrativeStaff;
import com.hospital.employee.exception.EmployeeNotFoundException;
import com.hospital.employee.repository.AdministrativeStaffRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AdministrativeStaffUseCaseImpl implements EmployeeUseCase<AdministrativeStaff> {

    private final AdministrativeStaffRepository staffRepository;

    public AdministrativeStaffUseCaseImpl(AdministrativeStaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public AdministrativeStaff create(AdministrativeStaff staff) {
        return staffRepository.save(staff);
    }

    @Override
    public AdministrativeStaff update(Long id, AdministrativeStaff staff) {
        AdministrativeStaff existing = findById(id);
        existing.setFirstName(staff.getFirstName());
        existing.setLastName(staff.getLastName());
        existing.setEmail(staff.getEmail());
        existing.setPhone(staff.getPhone());
        existing.setDepartment(staff.getDepartment());
        existing.setDepartment(staff.getDepartment());
        existing.setAddress(staff.getAddress());
        existing.setHireDate(staff.getHireDate());
        existing.setIsActive(staff.getIsActive());
        return staffRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        staffRepository.deleteById(id);
    }

    @Override
    public AdministrativeStaff findById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Administrative Staff not found with id: " + id));
    }

    @Override
    public List<AdministrativeStaff> findAll() {
        return staffRepository.findAll();
    }
}
