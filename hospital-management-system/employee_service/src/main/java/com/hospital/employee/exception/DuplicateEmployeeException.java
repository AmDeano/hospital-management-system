package com.hospital.employee.exception;

/**
 * Exception thrown when attempting to create an employee with duplicate information
 */
public class DuplicateEmployeeException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateEmployeeException(String message) {
        super(message);
    }

    public DuplicateEmployeeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateEmployeeException(Throwable cause) {
        super(cause);
    }
}