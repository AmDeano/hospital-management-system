package com.hospital.employee.service;

import com.hospital.employee.entity.Department;
import com.hospital.employee.exception.InvalidEmployeeDataException;
import com.hospital.employee.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentServiceImpl(DepartmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Department> findAll() {
        return repository.findAll();
    }

    @Override
    public Department save(Department department) {
        if (department.getName() == null || department.getName().isBlank()) {
            throw new InvalidEmployeeDataException("Department must have a name");
        }
        return repository.save(department);
    }
}
