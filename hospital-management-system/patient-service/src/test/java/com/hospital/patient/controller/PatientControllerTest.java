package com.hospital.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    private PatientDto samplePatient;

    @BeforeEach
    public void setup() {
        samplePatient = new PatientDto();
        samplePatient.setId("CIN123456");
        samplePatient.setNom("John Doe");
        samplePatient.setDateNaissance(LocalDate.of(2000, 1, 1));
        samplePatient.setEmail("john@example.com");
        samplePatient.setCin("CIN123456");
        samplePatient.setIsMinor(false);
    }

    @Test
    void shouldCreatePatient() throws Exception {
        when(patientService.createPatient(any(PatientDto.class))).thenReturn(samplePatient);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(samplePatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldGetAllPatients() throws Exception {
        when(patientService.getAllPatients()).thenReturn(Collections.singletonList(samplePatient));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("CIN123456"));
    }

    @Test
    void shouldGetPatientById() throws Exception {
        when(patientService.getPatientById("CIN123456")).thenReturn(samplePatient);

        mockMvc.perform(get("/api/patients/CIN123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("John Doe"));
    }

    @Test
    void shouldUpdatePatient() throws Exception {
        when(patientService.updatePatient(eq("CIN123456"), any(PatientDto.class)))
                .thenReturn(samplePatient);

        mockMvc.perform(put("/api/patients/CIN123456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(samplePatient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("CIN123456"));
    }

    @Test
    void shouldDeletePatient() throws Exception {
        doNothing().when(patientService).deletePatient("CIN123456");

        mockMvc.perform(delete("/api/patients/CIN123456"))
                .andExpect(status().isOk());
    }
}
