package com.hospital.employee.controller;

import com.hospital.employee.dto.EmployeeDto;
import com.hospital.employee.entity.EmployeeType;
import com.hospital.employee.entity.WorkDay;
import com.hospital.employee.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private EmployeeDto testEmployeeDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        objectMapper = new ObjectMapper();
        
        // Create test employee DTO
        testEmployeeDto = new EmployeeDto();
        testEmployeeDto.setMatricule("EMP001");
        testEmployeeDto.setNom("Doe");
        testEmployeeDto.setPrenom("John");
        testEmployeeDto.setDepartement("Cardiology");
        testEmployeeDto.setEmployeeType(EmployeeType.MEDICAL_STAFF);
        testEmployeeDto.setIsActive(true);
        testEmployeeDto.setDateEmbauche(LocalDate.now());
    }

    // Basic CRUD Operations Tests
    @Test
    void createEmployee_ShouldReturnCreatedEmployee() throws Exception {
        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(testEmployeeDto);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricule").value("EMP001"))
                .andExpect(jsonPath("$.nom").value("Doe"))
                .andExpect(jsonPath("$.prenom").value("John"));

        verify(employeeService, times(1)).createEmployee(any(EmployeeDto.class));
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].matricule").value("EMP001"));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void getEmployeeByMatricule_ShouldReturnEmployee() throws Exception {
        when(employeeService.getEmployeeByMatricule("EMP001")).thenReturn(testEmployeeDto);

        mockMvc.perform(get("/api/employees/EMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("EMP001"))
                .andExpect(jsonPath("$.nom").value("Doe"));

        verify(employeeService, times(1)).getEmployeeByMatricule("EMP001");
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() throws Exception {
        when(employeeService.updateEmployee(eq("EMP001"), any(EmployeeDto.class))).thenReturn(testEmployeeDto);

        mockMvc.perform(put("/api/employees/EMP001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("EMP001"));

        verify(employeeService, times(1)).updateEmployee(eq("EMP001"), any(EmployeeDto.class));
    }

    @Test
    void deleteEmployee_ShouldReturnNoContent() throws Exception {
        doNothing().when(employeeService).deleteEmployee("EMP001");

        mockMvc.perform(delete("/api/employees/EMP001"))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee("EMP001");
    }

    @Test
    void activateEmployee_ShouldReturnActivatedEmployee() throws Exception {
        when(employeeService.activateEmployee("EMP001")).thenReturn(testEmployeeDto);

        mockMvc.perform(patch("/api/employees/EMP001/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("EMP001"));

        verify(employeeService, times(1)).activateEmployee("EMP001");
    }

    // Employee Type Based Queries Tests
    @Test
    void getEmployeesByType_ShouldReturnEmployeesByType() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getEmployeesByType(EmployeeType.MEDICAL_STAFF)).thenReturn(employees);

        mockMvc.perform(get("/api/employees/type/DOCTOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].employeeType").value("DOCTOR"));

        verify(employeeService, times(1)).getEmployeesByType(EmployeeType.MEDICAL_STAFF);
    }

    @Test
    void getAdministrationEmployees_ShouldReturnAdministrationEmployees() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getAdministrationEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees/administration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).getAdministrationEmployees();
    }

    @Test
    void getMedicalStaff_ShouldReturnMedicalStaff() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getMedicalStaff()).thenReturn(employees);

        mockMvc.perform(get("/api/employees/medical-staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).getMedicalStaff();
    }

    // Department Based Queries Tests
    @Test
    void getEmployeesByDepartment_ShouldReturnEmployeesByDepartment() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getEmployeesByDepartment("Cardiology")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/department/Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].departement").value("Cardiology"));

        verify(employeeService, times(1)).getEmployeesByDepartment("Cardiology");
    }

    @Test
    void getEmployeesByDepartmentAndType_ShouldReturnFilteredEmployees() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getEmployeesByDepartmentAndType("Cardiology", EmployeeType.MEDICAL_STAFF))
                .thenReturn(employees);

        mockMvc.perform(get("/api/employees/department/Cardiology/type/DOCTOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1))
                .getEmployeesByDepartmentAndType("Cardiology", EmployeeType.MEDICAL_STAFF);
    }

    // Medical Staff Specific Queries Tests
    @Test
    void getMedicalStaffBySpeciality_ShouldReturnMedicalStaffBySpeciality() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getMedicalStaffBySpeciality("Cardiology")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/medical-staff/speciality/Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).getMedicalStaffBySpeciality("Cardiology");
    }

    @Test
    void getAvailableDoctorsByWorkDay_ShouldReturnAvailableDoctors() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getAvailableDoctorsByWorkDay(WorkDay.MONDAY)).thenReturn(employees);

        mockMvc.perform(get("/api/employees/doctors/available/MONDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).getAvailableDoctorsByWorkDay(WorkDay.MONDAY);
    }

    // Supervisor and Hierarchy Queries Tests
    @Test
    void getEmployeesBySupervisor_ShouldReturnEmployeesBySupervisor() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getEmployeesBySupervisor("SUP001")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/supervisor/SUP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).getEmployeesBySupervisor("SUP001");
    }

    // Status Based Queries Tests
    @Test
    void getActiveEmployees_ShouldReturnActiveEmployees() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getActiveEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).getActiveEmployees();
    }

    // Search Operations Tests
    @Test
    void searchEmployees_ShouldReturnSearchResults() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.searchEmployees(anyString(), anyString(), anyString(), 
                any(EmployeeType.class), anyBoolean())).thenReturn(employees);

        mockMvc.perform(get("/api/employees/search")
                .param("nom", "Doe")
                .param("prenom", "John")
                .param("departement", "Cardiology")
                .param("employeeType", "DOCTOR")
                .param("isActive", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).searchEmployees("Doe", "John", "Cardiology", 
                EmployeeType.MEDICAL_STAFF, true);
    }

    @Test
    void searchEmployeesByName_ShouldReturnSearchResults() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.searchEmployeesByName("John")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/search/name")
                .param("searchTerm", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).searchEmployeesByName("John");
    }

    // Date Range Queries Tests
    @Test
    void getEmployeesByHireDateRange_ShouldReturnEmployeesInDateRange() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        
        when(employeeService.getEmployeesByHireDateRange(startDate, endDate)).thenReturn(employees);

        mockMvc.perform(get("/api/employees/hire-date-range")
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).getEmployeesByHireDateRange(startDate, endDate);
    }

    // Statistics and Analytics Tests
    @Test
    void getEmployeeStatistics_ShouldReturnStatistics() throws Exception {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalEmployees", 100);
        statistics.put("activeEmployees", 95);
        statistics.put("doctorsCount", 30);
        
        when(employeeService.getEmployeeStatistics()).thenReturn(statistics);

        mockMvc.perform(get("/api/employees/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").value(100))
                .andExpect(jsonPath("$.activeEmployees").value(95))
                .andExpect(jsonPath("$.doctorsCount").value(30));

        verify(employeeService, times(1)).getEmployeeStatistics();
    }

    // Health check endpoint test
    @Test
    void healthCheck_ShouldReturnHealthMessage() throws Exception {
        mockMvc.perform(get("/api/employees/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee Service is running!"));
    }

    // Edge Cases and Error Scenarios
    @Test
    void createEmployee_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        EmployeeDto invalidEmployee = new EmployeeDto();
        // Empty/invalid employee data

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmployee)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchEmployees_WithNoParams_ShouldWork() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.searchEmployees(null, null, null, null, null)).thenReturn(employees);

        mockMvc.perform(get("/api/employees/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).searchEmployees(null, null, null, null, null);
    }
}