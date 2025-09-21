package com.hospital.employee.usecase;

import com.hospital.employee.entity.Employee;
import com.hospital.employee.exception.EmployeeNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

public abstract class AbstractEmployeeUseCase<T extends Employee> implements EmployeeUseCase<T> {

    protected final JpaRepository<T, Long> repository;

    protected AbstractEmployeeUseCase(JpaRepository<T, Long> repository) {
        this.repository = Objects.requireNonNull(repository, "repository required");
    }

    @Override
    public T create(T employee) {
        validateOnCreate(employee);
        return repository.save(employee);
    }

    @Override
    public T update(Long id, T employee) {
        T existing = findById(id);
        updateCommonFields(existing, employee);
        updateSpecificFields(existing, employee);
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        T existing = findById(id);
        beforeDelete(existing);
        repository.delete(existing);
    }

    @Override
    public T findById(Long id) {
        Optional<T> opt = repository.findById(id);
        return opt.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    /** Hook for validation when creating */
    protected void validateOnCreate(T employee) { }

    /** Hook for updating subtype-specific fields */
    protected void updateSpecificFields(T existing, T update) { }

    /** Hook before delete (business rules) */
    protected void beforeDelete(T employee) { }

    /** Shared field updates */
    protected void updateCommonFields(Employee existing, Employee update) {
        if (update.getMatricule() != null) existing.setMatricule(update.getMatricule());
        if (update.getFirstName() != null) existing.setFirstName(update.getFirstName());
        if (update.getLastName() != null) existing.setLastName(update.getLastName());
        if (update.getEmail() != null) existing.setEmail(update.getEmail());
        if (update.getPhone() != null) existing.setPhone(update.getPhone());
        if (update.getDepartment() != null) existing.setDepartment(update.getDepartment());
        if (update.getAddress() != null) existing.setAddress(update.getAddress());
        if (update.getHireDate() != null) existing.setHireDate(update.getHireDate());
        if (update.getIsActive() != null) existing.setIsActive(update.getIsActive());
        if (update.getRole() != null) existing.setRole(update.getRole());
    }
}
