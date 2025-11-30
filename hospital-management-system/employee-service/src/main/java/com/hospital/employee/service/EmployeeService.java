package com.hospital.employee.service;

import com.hospital.employee.entity.Employee;

import java.util.List;

public interface EmployeeService<T extends Employee> {
    T create(T employee);
    T update(Long id, T employee);
    void delete(Long id);
    T findById(Long id);
    List<T> findAll();
}
