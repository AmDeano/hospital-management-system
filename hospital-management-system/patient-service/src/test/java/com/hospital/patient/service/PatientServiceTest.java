package com.hospital.patient.service;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.entity.Patient;
import com.hospital.patient.exception.DuplicatePatientException;
import com.hospital.patient.exception.InvalidPatientDataException;
import com.hospital.patient.exception.PatientNotFoundException;
import com.hospital.patient.repository.PatientRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    private PatientDto adultDto;
    private PatientDto minorDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adultDto = new PatientDto();
        adultDto.setNom("John Doe");
        adultDto.setDateNaissance(LocalDate.of(1990, 1, 1));
        adultDto.setEmail("john@example.com");
        adultDto.setCin("CIN12345");
        adultDto.setNumeroSecuriteSociale("SSN123");
        adultDto.setIsMinor(false);

        minorDto = new PatientDto();
        minorDto.setNom("Jane Doe");
        minorDto.setDateNaissance(LocalDate.now().minusYears(10));
        minorDto.setEmail("jane@example.com");
        minorDto.setNumeroSecuriteSociale("SSN456");
        minorDto.setParentCin("CIN12345");
        minorDto.setIsMinor(true);
    }

    @Test
    void createPatient_successfulAdult() {
        when(patientRepository.existsByEmail(adultDto.getEmail())).thenReturn(false);
        when(patientRepository.existsByNumeroSecuriteSociale(adultDto.getNumeroSecuriteSociale())).thenReturn(false);
        when(patientRepository.existsByCin(adultDto.getCin())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> {
            Patient p = i.getArgument(0);
            p.setId(p.getCin());
            return p;
        });

        PatientDto saved = patientService.createPatient(adultDto);

        assertNotNull(saved);
        assertEquals("CIN12345", saved.getId());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createPatient_successfulMinor() {
        when(patientRepository.existsByEmail(minorDto.getEmail())).thenReturn(false);
        when(patientRepository.existsByNumeroSecuriteSociale(minorDto.getNumeroSecuriteSociale())).thenReturn(false);
        when(patientRepository.existsByCin(minorDto.getParentCin())).thenReturn(true);
        when(patientRepository.getNextMinorId()).thenReturn(5);
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> {
            Patient p = i.getArgument(0);
            p.setId("MIN-0005");
            return p;
        });

        PatientDto saved = patientService.createPatient(minorDto);

        assertNotNull(saved);
        assertTrue(saved.getId().startsWith("MIN-"));
        assertEquals("MIN-0005", saved.getId());
    }

    @Test
    void createPatient_duplicateEmail_throwsException() {
        when(patientRepository.existsByEmail(adultDto.getEmail())).thenReturn(true);
        assertThrows(DuplicatePatientException.class, () -> patientService.createPatient(adultDto));
    }

    @Test
    void createPatient_missingCINForAdult_throwsException() {
        adultDto.setCin(null);
        assertThrows(InvalidPatientDataException.class, () -> patientService.createPatient(adultDto));
    }

    @Test
    void createPatient_missingParentCinForMinor_throwsException() {
        minorDto.setParentCin(null);
        assertThrows(InvalidPatientDataException.class, () -> patientService.createPatient(minorDto));
    }

    @Test
    void getPatientById_found() {
        Patient patient = new Patient();
        patient.setId("CIN123");
        patient.setNom("John");

        when(patientRepository.findById("CIN123")).thenReturn(Optional.of(patient));

        PatientDto result = patientService.getPatientById("CIN123");

        assertNotNull(result);
        assertEquals("John", result.getNom());
    }

    @Test
    void getPatientById_notFound_throwsException() {
        when(patientRepository.findById("unknown")).thenReturn(Optional.empty());
        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById("unknown"));
    }

    @Test
    void updatePatient_successfulUpdate() {
        Patient existing = new Patient("John Doe", LocalDate.of(1990, 1, 1), "old@example.com", null, null, null, "CIN123", null);
        existing.setId("CIN123");

        PatientDto update = new PatientDto();
        update.setNom("Johnny Updated");
        update.setDateNaissance(LocalDate.of(1990, 1, 1));
        update.setEmail("new@example.com");
        update.setCin("CIN123");

        when(patientRepository.findById("CIN123")).thenReturn(Optional.of(existing));
        when(patientRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(existing);

        PatientDto result = patientService.updatePatient("CIN123", update);

        assertEquals("Johnny Updated", result.getNom());
    }

    @Test
    void deletePatient_existingId() {
        when(patientRepository.existsById("CIN123")).thenReturn(true);
        patientService.deletePatient("CIN123");
        verify(patientRepository).deleteById("CIN123");
    }

    @Test
    void deletePatient_notFound_throwsException() {
        when(patientRepository.existsById("NOT_FOUND")).thenReturn(false);
        assertThrows(PatientNotFoundException.class, () -> patientService.deletePatient("NOT_FOUND"));
    }
}
