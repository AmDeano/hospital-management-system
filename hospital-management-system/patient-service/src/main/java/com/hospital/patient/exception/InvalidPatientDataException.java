package com.hospital.patient.exception;

public class InvalidPatientDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidPatientDataException(String message) {
        super(message);
    }
}