package com.hospital.employee.usecase;

import com.hospital.employee.entity.Department;
import com.hospital.employee.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentUseCaseImpl implements DepartmentUseCase {

    private final DepartmentRepository repository;

    public DepartmentUseCaseImpl(DepartmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Department> findAll() {
        return repository.findAll();
    }

    @Override
    public Department save(Department department) {
        return repository.save(department);
    }
}
