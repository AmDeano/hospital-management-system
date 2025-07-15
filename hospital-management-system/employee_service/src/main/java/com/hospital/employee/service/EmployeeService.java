package com.hospital.employee.service;

import com.hospital.employee.dto.EmployeeDto;
import com.hospital.employee.entity.Employee;
import com.hospital.employee.entity.EmployeeType;
import com.hospital.employee.entity.WorkDay;
import com.hospital.employee.exception.EmployeeNotFoundException;
import com.hospital.employee.exception.DuplicateEmployeeException;
import com.hospital.employee.exception.InvalidEmployeeDataException;
import com.hospital.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Create a new employee
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        // Validate employee data
        validateEmployeeData(employeeDto);
        
        // Check for duplicates
        checkForDuplicates(employeeDto);
        
        // Generate matricule if not provided
        if (employeeDto.getMatricule() == null || employeeDto.getMatricule().trim().isEmpty()) {
            employeeDto.setMatricule(generateMatricule(employeeDto.getEmployeeType()));
        }
        
        Employee employee = convertToEntity(employeeDto);
        Employee savedEmployee = employeeRepository.save(employee);
        
        return convertToDto(savedEmployee);
    }

    // Get all employees
    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get employee by matricule
    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeByMatricule(String matricule) {
        Employee employee = employeeRepository.findById(matricule)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with matricule: " + matricule));
        return convertToDto(employee);
    }

    // Get employees by type
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByType(EmployeeType employeeType) {
        return employeeRepository.findByEmployeeType(employeeType)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get administration employees
    @Transactional(readOnly = true)
    public List<EmployeeDto> getAdministrationEmployees() {
        return getEmployeesByType(EmployeeType.ADMINISTRATION);
    }

    // Get medical staff
    @Transactional(readOnly = true)
    public List<EmployeeDto> getMedicalStaff() {
        return getEmployeesByType(EmployeeType.MEDICAL_STAFF);
    }

    // Get employees by department
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByDepartment(String departement) {
        return employeeRepository.findByDepartement(departement)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get employees by department and type
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByDepartmentAndType(String departement, EmployeeType employeeType) {
        return employeeRepository.findByDepartementAndEmployeeType(departement, employeeType)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get medical staff by speciality
    @Transactional(readOnly = true)
    public List<EmployeeDto> getMedicalStaffBySpeciality(String specialite) {
        return employeeRepository.findBySpecialite(specialite)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get employees by supervisor
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesBySupervisor(String supervisorMatricule) {
        return employeeRepository.findBySupervisorMatricule(supervisorMatricule)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get active employees
    @Transactional(readOnly = true)
    public List<EmployeeDto> getActiveEmployees() {
        return employeeRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get inactive employees
    @Transactional(readOnly = true)
    public List<EmployeeDto> getInactiveEmployees() {
        return employeeRepository.findByIsActiveFalse()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get available doctors for a specific day
    @Transactional(readOnly = true)
    public List<EmployeeDto> getAvailableDoctorsByWorkDay(WorkDay workDay) {
        return employeeRepository.findAvailableDoctorsByWorkDay(workDay)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get employees without supervisor
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesWithoutSupervisor() {
        return employeeRepository.findBySupervisorMatriculeIsNull()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get medical staff with license
    @Transactional(readOnly = true)
    public List<EmployeeDto> getMedicalStaffWithLicense() {
        return employeeRepository.findMedicalStaffWithLicence()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get active medical staff by speciality and department
    @Transactional(readOnly = true)
    public List<EmployeeDto> getActiveMedicalStaffBySpecialityAndDepartment(String specialite, String departement) {
        return employeeRepository.findActiveMedicalStaffBySpecialityAndDepartment(specialite, departement)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Search employees
    @Transactional(readOnly = true)
    public List<EmployeeDto> searchEmployees(String nom, String prenom, String departement, 
                                           EmployeeType employeeType, Boolean isActive) {
        return employeeRepository.searchEmployees(nom, prenom, departement, employeeType, isActive)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Search employees by name
    @Transactional(readOnly = true)
    public List<EmployeeDto> searchEmployeesByName(String searchTerm) {
        return employeeRepository.findByNomOrPrenomContainingIgnoreCase(searchTerm, searchTerm)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Search employees by full name
    @Transactional(readOnly = true)
    public List<EmployeeDto> searchEmployeesByFullName(String fullName) {
        return employeeRepository.findByFullNameContainingIgnoreCase(fullName)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Find employees by phone number
    @Transactional(readOnly = true)
    public List<EmployeeDto> findEmployeesByPhoneNumber(String telephone) {
        return employeeRepository.findByTelephone(telephone)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Update employee
    public EmployeeDto updateEmployee(String matricule, EmployeeDto employeeDto) {
        Employee existingEmployee = employeeRepository.findById(matricule)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with matricule: " + matricule));
        
        // Validate updated data
        validateEmployeeData(employeeDto);
        
        // Check for duplicates (excluding current employee)
        checkForDuplicatesExcluding(employeeDto, matricule);
        
        // Update employee fields
        updateEmployeeFields(existingEmployee, employeeDto);
        
        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return convertToDto(updatedEmployee);
    }

    // Delete employee (soft delete)
    public void deleteEmployee(String matricule) {
        Employee employee = employeeRepository.findById(matricule)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with matricule: " + matricule));
        
        employee.setIsActive(false);
        employeeRepository.save(employee);
    }

    // Permanently delete employee (hard delete)
    public void permanentlyDeleteEmployee(String matricule) {
        Employee employee = employeeRepository.findById(matricule)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with matricule: " + matricule));
        
        employeeRepository.delete(employee);
    }

    // Activate employee
    public EmployeeDto activateEmployee(String matricule) {
        Employee employee = employeeRepository.findById(matricule)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with matricule: " + matricule));
        
        employee.setIsActive(true);
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDto(savedEmployee);
    }

    // Deactivate employee
    public EmployeeDto deactivateEmployee(String matricule) {
        Employee employee = employeeRepository.findById(matricule)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with matricule: " + matricule));
        
        employee.setIsActive(false);
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDto(savedEmployee);
    }

    // Get employee statistics
    @Transactional(readOnly = true)
    public Map<String, Object> getEmployeeStatistics() {
        long totalEmployees = employeeRepository.count();
        long activeAdministration = employeeRepository.countByEmployeeTypeAndIsActive(EmployeeType.ADMINISTRATION);
        long activeMedicalStaff = employeeRepository.countByEmployeeTypeAndIsActive(EmployeeType.MEDICAL_STAFF);
        long activeEmployees = activeAdministration + activeMedicalStaff;
        long inactiveEmployees = totalEmployees - activeEmployees;
        
        Map<EmployeeType, Long> employeesByType = employeeRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Employee::getEmployeeType, Collectors.counting()));
        
        Map<String, Long> departmentStats = employeeRepository.findAll()
                .stream()
                .filter(Employee::getIsActive)
                .collect(Collectors.groupingBy(Employee::getDepartement, Collectors.counting()));
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalEmployees", totalEmployees);
        statistics.put("activeEmployees", activeEmployees);
        statistics.put("inactiveEmployees", inactiveEmployees);
        statistics.put("activeAdministration", activeAdministration);
        statistics.put("activeMedicalStaff", activeMedicalStaff);
        statistics.put("employeesByType", employeesByType);
        statistics.put("employeesByDepartment", departmentStats);
        
        return statistics;
    }

    // Get detailed statistics by department
    @Transactional(readOnly = true)
    public Map<String, Object> getDetailedStatisticsByDepartment() {
        List<Object[]> departmentStats = employeeRepository.getEmployeeStatisticsByDepartment();
        
        Map<String, Map<String, Long>> departmentEmployeeTypes = new HashMap<>();
        
        for (Object[] stat : departmentStats) {
            String department = (String) stat[0];
            Long count = (Long) stat[1];
            EmployeeType employeeType = (EmployeeType) stat[2];
            
            departmentEmployeeTypes.computeIfAbsent(department, k -> new HashMap<>())
                    .put(employeeType.name(), count);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("departmentBreakdown", departmentEmployeeTypes);
        
        return result;
    }

    // Get employees by hire date range
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByHireDateRange(LocalDate startDate, LocalDate endDate) {
        return employeeRepository.findByDateEmbaucheBetween(startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get employees hired in current month
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesHiredThisMonth() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return getEmployeesByHireDateRange(startOfMonth, endOfMonth);
    }

    // Get employees hired in current year
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesHiredThisYear() {
        LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
        LocalDate endOfYear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        return getEmployeesByHireDateRange(startOfYear, endOfYear);
    }

    // Count employees by department
    @Transactional(readOnly = true)
    public Long countEmployeesByDepartment(String departement) {
        return employeeRepository.countByDepartementAndIsActive(departement);
    }

    // Check if employee exists
    @Transactional(readOnly = true)
    public boolean employeeExists(String matricule) {
        return employeeRepository.existsById(matricule);
    }

    // Check if email exists
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return employeeRepository.existsByEmail(email);
    }

    // Check if CIN exists
    @Transactional(readOnly = true)
    public boolean cinExists(String cin) {
        return employeeRepository.existsByCin(cin);
    }

    // Check if license number exists
    @Transactional(readOnly = true)
    public boolean licenseNumberExists(String licenseNumber) {
        return employeeRepository.existsByLicenceNumber(licenseNumber);
    }

    // Helper methods
    private void validateEmployeeData(EmployeeDto employeeDto) {
        if (employeeDto.getNom() == null || employeeDto.getNom().trim().isEmpty()) {
            throw new InvalidEmployeeDataException("Employee name is required");
        }
        
        if (employeeDto.getPrenom() == null || employeeDto.getPrenom().trim().isEmpty()) {
            throw new InvalidEmployeeDataException("Employee first name is required");
        }
        
        if (employeeDto.getEmployeeType() == null) {
            throw new InvalidEmployeeDataException("Employee type is required");
        }
        
        if (employeeDto.getDepartement() == null || employeeDto.getDepartement().trim().isEmpty()) {
            throw new InvalidEmployeeDataException("Department is required");
        }
        
        if (employeeDto.getEmail() != null && !isValidEmail(employeeDto.getEmail())) {
            throw new InvalidEmployeeDataException("Invalid email format");
        }
        
        if (employeeDto.getTelephone() != null && !isValidPhoneNumber(employeeDto.getTelephone())) {
            throw new InvalidEmployeeDataException("Invalid phone number format");
        }
        
        if (employeeDto.getEmployeeType() == EmployeeType.MEDICAL_STAFF) {
            if (employeeDto.getSpecialite() == null || employeeDto.getSpecialite().trim().isEmpty()) {
                throw new InvalidEmployeeDataException("Speciality is required for medical staff");
            }
        }
        
        if (employeeDto.getDateNaissance() != null && employeeDto.getDateNaissance().isAfter(LocalDate.now().minusYears(18))) {
            throw new InvalidEmployeeDataException("Employee must be at least 18 years old");
        }
    }

    private void checkForDuplicates(EmployeeDto employeeDto) {
        if (employeeDto.getMatricule() != null && employeeRepository.existsById(employeeDto.getMatricule())) {
            throw new DuplicateEmployeeException("Employee with matricule " + employeeDto.getMatricule() + " already exists");
        }
        
        if (employeeDto.getEmail() != null && employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new DuplicateEmployeeException("Employee with email " + employeeDto.getEmail() + " already exists");
        }
        
        if (employeeDto.getCin() != null && employeeRepository.existsByCin(employeeDto.getCin())) {
            throw new DuplicateEmployeeException("Employee with CIN " + employeeDto.getCin() + " already exists");
        }
        
        if (employeeDto.getLicenceNumber() != null && employeeRepository.existsByLicenceNumber(employeeDto.getLicenceNumber())) {
            throw new DuplicateEmployeeException("Employee with license number " + employeeDto.getLicenceNumber() + " already exists");
        }
    }

    private void checkForDuplicatesExcluding(EmployeeDto employeeDto, String excludeMatricule) {
        // Check email uniqueness
        if (employeeDto.getEmail() != null) {
            employeeRepository.findByEmail(employeeDto.getEmail())
                .ifPresent(employee -> {
                    if (!employee.getMatricule().equals(excludeMatricule)) {
                        throw new DuplicateEmployeeException("Employee with email " + employeeDto.getEmail() + " already exists");
                    }
                });
        }
        
        // Check CIN uniqueness
        if (employeeDto.getCin() != null) {
            employeeRepository.findByCin(employeeDto.getCin())
                .ifPresent(employee -> {
                    if (!employee.getMatricule().equals(excludeMatricule)) {
                        throw new DuplicateEmployeeException("Employee with CIN " + employeeDto.getCin() + " already exists");
                    }
                });
        }
        
        // Check license number uniqueness
        if (employeeDto.getLicenceNumber() != null) {
            employeeRepository.findByLicenceNumber(employeeDto.getLicenceNumber())
                .ifPresent(employee -> {
                    if (!employee.getMatricule().equals(excludeMatricule)) {
                        throw new DuplicateEmployeeException("Employee with license number " + employeeDto.getLicenceNumber() + " already exists");
                    }
                });
        }
    }

    private String generateMatricule(Enum<com.hospital.employee.dto.EmployeeType> employeeType) {
        String prefix = employeeType == EmployeeType.MEDICAL_STAFF ? "MED" : "ADM";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
        
        // Get the next sequence number for this type and month
        String pattern = prefix + datePart + "%";
        List<Employee> employees = employeeRepository.findByMatriculePattern(pattern);
        
        return String.format("%s%s%03d", prefix, datePart, employees.size() + 1);
    }

    private void updateEmployeeFields(Employee existingEmployee, EmployeeDto employeeDto) {
        existingEmployee.setNom(employeeDto.getNom());
        existingEmployee.setPrenom(employeeDto.getPrenom());
        existingEmployee.setPoste(employeeDto.getPoste());
        existingEmployee.setDepartement(employeeDto.getDepartement());
        existingEmployee.setEmployeeType(employeeDto.getEmployeeType());
        existingEmployee.setEmail(employeeDto.getEmail());
        existingEmployee.setTelephone(employeeDto.getTelephone());
        existingEmployee.setAdresse(employeeDto.getAdresse());
        existingEmployee.setDateNaissance(employeeDto.getDateNaissance());
        existingEmployee.setNumeroSecuriteSociale(employeeDto.getNumeroSecuriteSociale());
        existingEmployee.setCin(employeeDto.getCin());
        existingEmployee.setSpecialite(employeeDto.getSpecialite());
        existingEmployee.setLicenceNumber(employeeDto.getLicenceNumber());
        existingEmployee.setSupervisorMatricule(employeeDto.getSupervisorMatricule());
        existingEmployee.setWorkDays(employeeDto.getWorkDays());
        existingEmployee.setShiftStart(employeeDto.getShiftStart());
        existingEmployee.setShiftEnd(employeeDto.getShiftEnd());
        if (employeeDto.getIsActive() != null) {
            existingEmployee.setIsActive(employeeDto.getIsActive());
        }
    }

    private Employee convertToEntity(EmployeeDto dto) {
        Employee employee = new Employee();
        employee.setMatricule(dto.getMatricule());
        employee.setNom(dto.getNom());
        employee.setPrenom(dto.getPrenom());
        employee.setPoste(dto.getPoste());
        employee.setDepartement(dto.getDepartement());
        employee.setEmployeeType(dto.getEmployeeType());
        employee.setEmail(dto.getEmail());
        employee.setTelephone(dto.getTelephone());
        employee.setAdresse(dto.getAdresse());
        employee.setDateNaissance(dto.getDateNaissance());
        employee.setNumeroSecuriteSociale(dto.getNumeroSecuriteSociale());
        employee.setCin(dto.getCin());
        employee.setSpecialite(dto.getSpecialite());
        employee.setLicenceNumber(dto.getLicenceNumber());
        employee.setSupervisorMatricule(dto.getSupervisorMatricule());
        employee.setWorkDays(dto.getWorkDays());
        employee.setShiftStart(dto.getShiftStart());
        employee.setShiftEnd(dto.getShiftEnd());
        employee.setDateEmbauche(dto.getDateEmbauche() != null ? dto.getDateEmbauche() : LocalDate.now());
        employee.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return employee;
    }

    private EmployeeDto convertToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setMatricule(employee.getMatricule());
        dto.setNom(employee.getNom());
        dto.setPrenom(employee.getPrenom());
        dto.setPoste(employee.getPoste());
        dto.setDepartement(employee.getDepartement());
        dto.setEmployeeType(employee.getEmployeeType());
        dto.setEmail(employee.getEmail());
        dto.setTelephone(employee.getTelephone());
        dto.setAdresse(employee.getAdresse());
        dto.setDateNaissance(employee.getDateNaissance());
        dto.setNumeroSecuriteSociale(employee.getNumeroSecuriteSociale());
        dto.setCin(employee.getCin());
        dto.setSpecialite(employee.getSpecialite());
        dto.setLicenceNumber(employee.getLicenceNumber());
        dto.setSupervisorMatricule(employee.getSupervisorMatricule());
        dto.setWorkDays(employee.getWorkDays());
        dto.setShiftStart(employee.getShiftStart());
        dto.setShiftEnd(employee.getShiftEnd());
        dto.setDateEmbauche(employee.getDateEmbauche());
        dto.setIsActive(employee.getIsActive());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());
        return dto;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^\\+?[0-9\\s\\-\\(\\)]{8,15}$");
    }
}