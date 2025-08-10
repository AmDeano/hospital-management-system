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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private static final String EMP_MATRICULE = "EMP001";
    private static final String DEPARTMENT = "Cardiology";
    private static final String SUPERVISOR_MATRICULE = "SUP001";

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
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        testEmployeeDto = createTestEmployeeDto();
    }

    private EmployeeDto createTestEmployeeDto() {
        final EmployeeDto dto = new EmployeeDto();
        dto.setMatricule(EMP_MATRICULE);
        dto.setNom("Doe");
        dto.setPrenom("John");
        dto.setDepartement(DEPARTMENT);
        dto.setEmployeeType(EmployeeType.ADMINISTRATION);
        dto.setIsActive(true);
        dto.setDateEmbauche(LocalDate.of(2023, 1, 15));
        return dto;
    }

    private List<EmployeeDto> singletonList() {
        return Collections.singletonList(testEmployeeDto);
    }

    /* ======= Create/Update/Delete ======= */

    @Test
    void createEmployee_ShouldReturnCreatedEmployee() throws Exception {
        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(testEmployeeDto);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.matricule").value(EMP_MATRICULE))
            .andExpect(jsonPath("$.nom").value("Doe"))
            .andExpect(jsonPath("$.prenom").value("John"));

        verify(employeeService).createEmployee(any(EmployeeDto.class));
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() throws Exception {
        when(employeeService.updateEmployee(eq(EMP_MATRICULE), any(EmployeeDto.class))).thenReturn(testEmployeeDto);

        mockMvc.perform(put("/api/employees/{matricule}", EMP_MATRICULE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matricule").value(EMP_MATRICULE));

        verify(employeeService).updateEmployee(eq(EMP_MATRICULE), any(EmployeeDto.class));
    }

    @Test
    void deleteEmployee_ShouldReturnNoContent() throws Exception {
        doNothing().when(employeeService).deleteEmployee(EMP_MATRICULE);

        mockMvc.perform(delete("/api/employees/{matricule}", EMP_MATRICULE))
            .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(EMP_MATRICULE);
    }

    @Test
    void activateEmployee_ShouldReturnActivatedEmployee() throws Exception {
        when(employeeService.activateEmployee(EMP_MATRICULE)).thenReturn(testEmployeeDto);

        mockMvc.perform(patch("/api/employees/{matricule}/activate", EMP_MATRICULE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matricule").value(EMP_MATRICULE));

        verify(employeeService).activateEmployee(EMP_MATRICULE);
    }

    /* ======= Retrieval ======= */

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].matricule").value(EMP_MATRICULE));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployeeByMatricule_ShouldReturnEmployee() throws Exception {
        when(employeeService.getEmployeeByMatricule(EMP_MATRICULE)).thenReturn(testEmployeeDto);

        mockMvc.perform(get("/api/employees/{matricule}", EMP_MATRICULE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matricule").value(EMP_MATRICULE))
            .andExpect(jsonPath("$.nom").value("Doe"));

        verify(employeeService).getEmployeeByMatricule(EMP_MATRICULE);
    }

    @Test
    void getEmployeesByType_ShouldReturnEmployeesByType() throws Exception {
        when(employeeService.getEmployeesByType(EmployeeType.MEDICAL_STAFF)).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/type/{employeeType}", "DOCTOR"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));

        verify(employeeService).getEmployeesByType(EmployeeType.MEDICAL_STAFF);
    }

    @Test
    void getAdministrationEmployees_ShouldReturnAdministrationEmployees() throws Exception {
        when(employeeService.getAdministrationEmployees()).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/administration"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).getAdministrationEmployees();
    }

    @Test
    void getMedicalStaff_ShouldReturnMedicalStaff() throws Exception {
        when(employeeService.getMedicalStaff()).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/medical-staff"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).getMedicalStaff();
    }

    @Test
    void getEmployeesByDepartment_ShouldReturnEmployeesByDepartment() throws Exception {
        when(employeeService.getEmployeesByDepartment(DEPARTMENT)).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/department/{departement}", DEPARTMENT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));

        verify(employeeService).getEmployeesByDepartment(DEPARTMENT);
    }

    @Test
    void getEmployeesByDepartmentAndType_ShouldReturnFilteredEmployees() throws Exception {
        when(employeeService.getEmployeesByDepartmentAndType(DEPARTMENT, EmployeeType.MEDICAL_STAFF))
            .thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/department/{departement}/type/{employeeType}", DEPARTMENT, "DOCTOR"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).getEmployeesByDepartmentAndType(DEPARTMENT, EmployeeType.MEDICAL_STAFF);
    }

    @Test
    void getMedicalStaffBySpeciality_ShouldReturnMedicalStaffBySpeciality() throws Exception {
        when(employeeService.getMedicalStaffBySpeciality(DEPARTMENT)).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/medical-staff/speciality/{specialite}", DEPARTMENT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).getMedicalStaffBySpeciality(DEPARTMENT);
    }

    @Test
    void getAvailableDoctorsByWorkDay_ShouldReturnAvailableDoctors() throws Exception {
        when(employeeService.getAvailableDoctorsByWorkDay(WorkDay.MONDAY)).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/doctors/available/{workDay}", "MONDAY"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).getAvailableDoctorsByWorkDay(WorkDay.MONDAY);
    }

    @Test
    void getEmployeesBySupervisor_ShouldReturnEmployeesBySupervisor() throws Exception {
        when(employeeService.getEmployeesBySupervisor(SUPERVISOR_MATRICULE)).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/supervisor/{supervisorMatricule}", SUPERVISOR_MATRICULE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).getEmployeesBySupervisor(SUPERVISOR_MATRICULE);
    }

    @Test
    void getActiveEmployees_ShouldReturnActiveEmployees() throws Exception {
        when(employeeService.getActiveEmployees()).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).getActiveEmployees();
    }

    /* ======= Search ======= */

    @Test
    void searchEmployees_WithAllParams_ShouldReturnSearchResults() throws Exception {
        when(employeeService.searchEmployees(
                eq("Doe"),
                eq("John"),
                eq(DEPARTMENT),
                eq(EmployeeType.MEDICAL_STAFF),
                eq(true)
            )).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/search")
                .param("nom", "Doe")
                .param("prenom", "John")
                .param("departement", DEPARTMENT)
                .param("employeeType", "DOCTOR")
                .param("isActive", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).searchEmployees("Doe", "John", DEPARTMENT, EmployeeType.MEDICAL_STAFF, true);
    }

    @Test
    void searchEmployees_WithNoParams_ShouldWork() throws Exception {
        when(employeeService.searchEmployees(null, null, null, null, null)).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/search"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).searchEmployees(null, null, null, null, null);
    }

    @Test
    void searchEmployeesByName_ShouldReturnSearchResults() throws Exception {
        when(employeeService.searchEmployeesByName("John")).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/search/name")
                .param("searchTerm", "John"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).searchEmployeesByName("John");
    }

    /* ======= Other ======= */

    @Test
    void getEmployeesByHireDateRange_ShouldReturnEmployeesInDateRange() throws Exception {
        final LocalDate startDate = LocalDate.of(2023, 1, 1);
        final LocalDate endDate = LocalDate.of(2023, 12, 31);

        when(employeeService.getEmployeesByHireDateRange(startDate, endDate)).thenReturn(singletonList());

        mockMvc.perform(get("/api/employees/hire-date-range")
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-12-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        verify(employeeService).getEmployeesByHireDateRange(startDate, endDate);
    }

    @Test
    void getEmployeeStatistics_ShouldReturnStatistics() throws Exception {
        final Map<String, Object> statistics = new HashMap<>();
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

    @Test
    void healthCheck_ShouldReturnHealthMessage() throws Exception {
        mockMvc.perform(get("/api/employees/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("Employee Service is running!"));
    }
}
