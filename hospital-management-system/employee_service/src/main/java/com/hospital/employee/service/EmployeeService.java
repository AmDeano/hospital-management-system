package com.hospital.employee.service;

import com.hospital.employee.dto.EmployeeDto;
import com.hospital.employee.entity.Employee;
import com.hospital.employee.exception.EmployeeNotFoundException;
import com.hospital.employee.exception.DuplicateEmployeeException;
import com.hospital.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeDto createEmployee(EmployeeDto dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmployeeException("Employee with email " + dto.getEmail() + " already exists");
        }
        Employee emp = toEntity(dto);
        Employee saved = employeeRepository.save(emp);
        return toDto(saved);
    }

    public EmployeeDto getEmployeeByMatricule(String matricule) {
        return toDto(employeeRepository.findById(matricule)
            .orElseThrow(() -> new EmployeeNotFoundException("Not found: " + matricule)));
    }

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public void deleteEmployee(String matricule) {
        if (!employeeRepository.existsById(matricule)) {
            throw new EmployeeNotFoundException("Not found: " + matricule);
        }
        employeeRepository.deleteById(matricule);
    }

    private Employee toEntity(EmployeeDto dto) {
        Employee e = new Employee();
        e.setMatricule(dto.getMatricule());
        e.setNom(dto.getNom());
        e.setPoste(dto.getPoste());
        e.setDepartement(dto.getDepartement());
        e.setTelephone(dto.getTelephone());
        e.setEmail(dto.getEmail());
        return e;
    }

    private EmployeeDto toDto(Employee e) {
        EmployeeDto dto = new EmployeeDto();
        dto.setMatricule(e.getMatricule());
        dto.setNom(e.getNom());
        dto.setPoste(e.getPoste());
        dto.setDepartement(e.getDepartement());
        dto.setTelephone(e.getTelephone());
        dto.setEmail(e.getEmail());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}
