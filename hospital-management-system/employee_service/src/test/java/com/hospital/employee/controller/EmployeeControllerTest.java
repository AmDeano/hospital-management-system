package com.hospital.employee.controller;

import com.hospital.employee.dto.EmployeeDto;
import com.hospital.employee.entity.EmployeeType;
import com.hospital.employee.entity.WorkDay;
import com.hospital.employee.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        objectMapper.registerModule(new JavaTimeModule());
        
        // Create test employee DTO
        testEmployeeDto = createTestEmployeeDto();
    }

    private EmployeeDto createTestEmployeeDto() {
        EmployeeDto dto = new EmployeeDto();
        dto.setMatricule("EMP001");
        dto.setNom("Doe");
        dto.setPrenom("John");
        dto.setDepartement("Cardiology");
        dto.setEmployeeType(EmployeeType.ADMINISTRATION);
        dto.setIsActive(true);
        dto.setDateEmbauche(LocalDate.of(2023, 1, 15));
        return dto;
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

        verify(employeeService).createEmployee(any(EmployeeDto.class));
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].matricule").value("EMP001"));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployeeByMatricule_ShouldReturnEmployee() throws Exception {
        when(employeeService.getEmployeeByMatricule("EMP001")).thenReturn(testEmployeeDto);

        mockMvc.perform(get("/api/employees/EMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("EMP001"))
                .andExpect(jsonPath("$.nom").value("Doe"));

        verify(employeeService).getEmployeeByMatricule("EMP001");
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() throws Exception {
        when(employeeService.updateEmployee(eq("EMP001"), any(EmployeeDto.class))).thenReturn(testEmployeeDto);

        mockMvc.perform(put("/api/employees/EMP001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("EMP001"));

        verify(employeeService).updateEmployee(eq("EMP001"), any(EmployeeDto.class));
    }

    @Test
    void deleteEmployee_ShouldReturnNoContent() throws Exception {
        doNothing().when(employeeService).deleteEmployee("EMP001");

        mockMvc.perform(delete("/api/employees/EMP001"))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee("EMP001");
    }

    @Test
    void activateEmployee_ShouldReturnActivatedEmployee() throws Exception {
        when(employeeService.activateEmployee("EMP001")).thenReturn(testEmployeeDto);

        mockMvc.perform(patch("/api/employees/EMP001/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("EMP001"));

        verify(employeeService).activateEmployee("EMP001");
    }

    // Employee Type Based Queries Tests
    @Test
    void getEmployeesByType_ShouldReturnEmployeesByType() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getEmployeesByType(EmployeeType.ADMINISTRATION)).thenReturn(employees);

        mockMvc.perform(get("/api/employees/type/DOCTOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(employeeService).getEmployeesByType(EmployeeType.ADMINISTRATION);
    }

    @Test
    void getAdministrationEmployees_ShouldReturnAdministrationEmployees() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getAdministrationEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees/administration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService).getAdministrationEmployees();
    }

    @Test
    void getMedicalStaff_ShouldReturnMedicalStaff() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getMedicalStaff()).thenReturn(employees);

        mockMvc.perform(get("/api/employees/medical-staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService).getMedicalStaff();
    }

    // Department Based Queries Tests
    @Test
    void getEmployeesByDepartment_ShouldReturnEmployeesByDepartment() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getEmployeesByDepartment("Cardiology")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/department/Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(employeeService).getEmployeesByDepartment("Cardiology");
    }

    @Test
    void getEmployeesByDepartmentAndType_ShouldReturnFilteredEmployees() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getEmployeesByDepartmentAndType("Cardiology", EmployeeType.ADMINISTRATION))
                .thenReturn(employees);

        mockMvc.perform(get("/api/employees/department/Cardiology/type/DOCTOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService).getEmployeesByDepartmentAndType("Cardiology", EmployeeType.ADMINISTRATION);
    }

    // Medical Staff Specific Queries Tests
    @Test
    void getMedicalStaffBySpeciality_ShouldReturnMedicalStaffBySpeciality() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getMedicalStaffBySpeciality("Cardiology")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/medical-staff/speciality/Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService).getMedicalStaffBySpeciality("Cardiology");
    }

    @Test
    void getAvailableDoctorsByWorkDay_ShouldReturnAvailableDoctors() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getAvailableDoctorsByWorkDay(WorkDay.MONDAY)).thenReturn(employees);

        mockMvc.perform(get("/api/employees/doctors/available/MONDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService).getAvailableDoctorsByWorkDay(WorkDay.MONDAY);
    }

    // Supervisor and Hierarchy Queries Tests
    @Test
    void getEmployeesBySupervisor_ShouldReturnEmployeesBySupervisor() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getEmployeesBySupervisor("SUP001")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/supervisor/SUP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService).getEmployeesBySupervisor("SUP001");
    }

    // Status Based Queries Tests
    @Test
    void getActiveEmployees_ShouldReturnActiveEmployees() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.getActiveEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService).getActiveEmployees();
    }

    // Search Operations Tests
    @Test
    void searchEmployees_WithAllParams_ShouldReturnSearchResults() throws Exception {
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

        verify(employeeService).searchEmployees("Doe", "John", "Cardiology", 
                EmployeeType.ADMINISTRATION, true);
    }

    @Test
    void searchEmployees_WithNoParams_ShouldWork() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.searchEmployees(isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(employees);

        mockMvc.perform(get("/api/employees/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService).searchEmployees(null, null, null, null, null);
    }

    @Test
    void searchEmployeesByName_ShouldReturnSearchResults() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(testEmployeeDto);
        when(employeeService.searchEmployeesByName("John")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/search/name")
                .param("searchTerm", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService).searchEmployeesByName("John");
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

        verify(employeeService).getEmployeesByHireDateRange(startDate, endDate);
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

        verify(employeeService).getEmployeeStatistics();
    }

    // Health check endpoint test
    @Test
    void healthCheck_ShouldReturnHealthMessage() throws Exception {
        mockMvc.perform(get("/api/employees/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee Service is running!"));
    }
}