package com.hospital.employee.controller;

import java.util.List;

//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hospital.employee.entity.Department;
import com.hospital.employee.usecase.DepartmentUseCase;

@RestController
@RequestMapping("/employee-service/api/departments")
public class DepartmentController {

    private final DepartmentUseCase useCase;

    public DepartmentController(DepartmentUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    //@PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public List<Department> all() {
        return useCase.findAll();
    }

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public Department create(@RequestBody Department d) {
        return useCase.save(d);
    }
}
