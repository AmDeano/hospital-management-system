package com.hospital.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreatePatient() throws Exception {
        // Create test data
        PatientDto patientDto = new PatientDto();
        patientDto.setCin("F422181");
        patientDto.setNom("John Doe");
        patientDto.setEmail("john.doe@example.com");
        patientDto.setDateNaissance(LocalDate.of(1990, 1, 1));
        patientDto.setNumeroTelephone("123456789");
        patientDto.setAdresse("123 Main St");
        patientDto.setNumeroSecuriteSociale("123-45-6789");

        PatientDto savedPatient = new PatientDto();
        savedPatient.setCin("F422181");
        savedPatient.setNom("John Doe");
        savedPatient.setEmail("john.doe@example.com");
        savedPatient.setDateNaissance(LocalDate.of(1990, 1, 1));
        savedPatient.setNumeroTelephone("123456789");
        savedPatient.setAdresse("123 Main St");
        savedPatient.setNumeroSecuriteSociale("123-45-6789");

        when(patientService.createPatient(any(PatientDto.class))).thenReturn(savedPatient);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void testGetAllPatients() throws Exception {
        PatientDto patient1 = new PatientDto();
        patient1.setCin("F422181");
        patient1.setNom("John Doe");
        patient1.setEmail("john.doe@example.com");

        PatientDto patient2 = new PatientDto();
        patient2.setCin("FA70895");
        patient2.setNom("Jane Smith");
        patient2.setEmail("jane.smith@example.com");

        List<PatientDto> patients = Arrays.asList(patient1, patient2);

        when(patientService.getAllPatients()).thenReturn(patients);

        mockMvc.perform(get("/api/patients"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nom").value("John Doe"))
                .andExpect(jsonPath("$[1].nom").value("Jane Smith"));
    }

    @Test
    public void testGetPatientById() throws Exception {
        PatientDto patient = new PatientDto();
        patient.setCin("F422181");
        patient.setNom("John Doe");
        patient.setEmail("john.doe@example.com");

        when(patientService.getPatientById(toString())).thenReturn(patient);

        mockMvc.perform(get("/api/patients/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("John Doe"));
    }

    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/patients/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Patient Service is running!"));
    }
}