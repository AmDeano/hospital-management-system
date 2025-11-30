-- ============================================================
-- Schema for employee-service
-- ============================================================
CREATE DATABASE IF NOT EXISTS employeedb
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE employeedb;

-- Create departments table
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    supervisor_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_department_name UNIQUE (name),
    INDEX idx_department_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create employees table (SINGLE_TABLE inheritance strategy)
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricule VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    role VARCHAR(50),
    department_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    hire_date DATE,
    address VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Discriminator column for SINGLE_TABLE inheritance
    employee_type VARCHAR(50) NOT NULL,

    -- Doctor-specific columns
    specialization VARCHAR(255),
    license_number VARCHAR(255),
    medical_degree VARCHAR(255),

    -- Nurse-specific columns
    shift VARCHAR(50),
    nursing_license VARCHAR(255),

    -- Receptionist-specific columns
    desk_number INT,

    -- Administrative Staff-specific columns
    department_area VARCHAR(255),

    -- Observator-specific columns
    assigned_area VARCHAR(255),

    CONSTRAINT uk_employee_matricule UNIQUE (matricule),
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id)
        REFERENCES departments(id) ON DELETE SET NULL,
    CONSTRAINT chk_employee_type CHECK (
        employee_type IN ('DOCTOR', 'NURSE', 'RECEPTIONIST', 'ADMINISTRATIVE_STAFF', 'OBSERVATOR', 'EMPLOYEE')
    ),
    INDEX idx_employee_matricule (matricule),
    INDEX idx_employee_email (email),
    INDEX idx_employee_type (employee_type),
    INDEX idx_employee_department (department_id),
    INDEX idx_employee_active (is_active),
    INDEX idx_employee_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add foreign key constraint for department supervisor_id (self-referencing)
ALTER TABLE departments
ADD CONSTRAINT fk_department_supervisor
FOREIGN KEY (supervisor_id) REFERENCES employees(id) ON DELETE SET NULL;

-- Insert default department
INSERT INTO departments (name)
VALUES ('General') ON DUPLICATE KEY UPDATE name=name;

-- Insert sample employees
INSERT INTO employees (matricule, first_name, last_name, email, phone, role, employee_type, is_active, hire_date, address)
VALUES
    ('DR001', 'John', 'Smith', 'john.smith@hospital.com', '+1234567890', 'DOCTOR', 'DOCTOR', TRUE, CURDATE(), '123 Medical St'),
    ('NR001', 'Jane', 'Doe', 'jane.doe@hospital.com', '+0987654321', 'NURSE', 'NURSE', TRUE, CURDATE(), '456 Care Ave')
ON DUPLICATE KEY UPDATE matricule=matricule;

-- Update employees with specialization and license (Doctor)
UPDATE employees
SET specialization = 'Cardiology', license_number = 'LIC001', medical_degree = 'MD'
WHERE employee_type = 'DOCTOR' AND matricule = 'DR001';

-- Update employees with shift and nursing license (Nurse)
UPDATE employees
SET shift = 'DAY', nursing_license = 'NURSE001'
WHERE employee_type = 'NURSE' AND matricule = 'NR001';

-- Create indexes for performance
CREATE INDEX idx_employee_specialization ON employees(specialization);
CREATE INDEX idx_employee_shift ON employees(shift);
CREATE INDEX idx_employee_license ON employees(license_number);

-- ============================================================
-- Doctor-Specific Tables
-- ============================================================

-- Doctor Schedules (working hours and shifts)
CREATE TABLE IF NOT EXISTS doctor_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_doctor_schedule_doctor FOREIGN KEY (doctor_id)
        REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT chk_day_of_week CHECK (
        day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')
    ),
    UNIQUE KEY uk_doctor_day (doctor_id, day_of_week),
    INDEX idx_doctor_schedule_doctor (doctor_id),
    INDEX idx_doctor_schedule_available (is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Doctor Availability Slots (specific time slots)
CREATE TABLE IF NOT EXISTS doctor_availability_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    available_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_availability_doctor FOREIGN KEY (doctor_id)
        REFERENCES employees(id) ON DELETE CASCADE,
    INDEX idx_availability_doctor (doctor_id),
    INDEX idx_availability_date (available_date),
    INDEX idx_availability_available (is_available),
    INDEX idx_availability_doctor_date (doctor_id, available_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;