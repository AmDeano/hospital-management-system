package com.hospital.employee.service;

import com.hospital.employee.entity.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> findAll();
    Department save(Department department);
}
