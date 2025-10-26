package com.hospital.employee.usecase;

import com.hospital.employee.entity.Department;
import java.util.List;

public interface DepartmentUseCase {
    List<Department> findAll();
    Department save(Department department);
}
