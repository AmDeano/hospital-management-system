package com.hospital.patient.exception;

public class DuplicatePatientException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicatePatientException(String message) {
        super(message);
    }
}