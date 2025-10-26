package com.hospital.auth.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PatientRegisterRequest(
    String firstName,
    String lastName,
    @Email String email,
    @NotBlank String password,
    LocalDate dateNaissance,
    String numeroTelephone,
    String adresse,
    String numeroSecuriteSociale,
    String CIN,
    String parentCin
) {}
