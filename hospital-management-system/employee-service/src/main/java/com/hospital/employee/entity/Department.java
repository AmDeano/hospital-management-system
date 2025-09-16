package com.hospital.employee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Department name is required")
    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String name; // "Cardiology", "HR", "Logistics", "Reception"

    @OneToOne
    @JoinColumn(name = "supervisor_id")
    private Employee supervisor;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();

    // Constructor with parameters
//    public Department(String name) {
//        this.name = name;
//        this.employees = new ArrayList<>();
//    }
//
//    public Department(String name, Employee supervisor) {
//        this.name = name;
//        this.supervisor = supervisor;
//        this.employees = new ArrayList<>();
//    }

    // Utility methods
    public void addEmployee(Employee employee) {
        if (employees == null) {
            employees = new ArrayList<>();
        }
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee) {
        if (employees != null) {
            employees.remove(employee);
            employee.setDepartment(null);
        }
    }

    public int getEmployeeCount() {
        return employees != null ? employees.size() : 0;
    }
}