package com.hospital.employee.exception;

/**
 * Exception thrown when employee data is invalid or doesn't meet business rules
 */
public class InvalidEmployeeDataException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidEmployeeDataException(String message) {
        super(message);
    }

    public InvalidEmployeeDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEmployeeDataException(Throwable cause) {
        super(cause);
    }
}