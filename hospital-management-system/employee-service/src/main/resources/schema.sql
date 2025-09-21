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
    CONSTRAINT uk_department_name UNIQUE (name)
);

-- Create employees table (single table inheritance)
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_type VARCHAR(31) NOT NULL, -- Discriminator column
    matricule VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    role VARCHAR(50), -- ENUM stored as VARCHAR
    department_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    hire_date DATE,
    address VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Doctor-specific fields
    specialization VARCHAR(255),
    license_number VARCHAR(255),
    medical_degree VARCHAR(255),
    
    -- Nurse-specific fields
    shift VARCHAR(50),
    nursing_license VARCHAR(255),
    
    -- Administrative Staff-specific fields
    department_area VARCHAR(255),
    
    -- Receptionist-specific fields
    desk_number VARCHAR(50),
    
    -- Observator-specific fields
    assigned_area VARCHAR(255),
    
    -- Foreign key constraints
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT uk_employee_matricule UNIQUE (matricule),
    
    -- Conditional constraints based on employee type
    CONSTRAINT chk_doctor_fields CHECK (
        employee_type != 'DOCTOR' OR (
            specialization IS NOT NULL AND 
            license_number IS NOT NULL AND 
            medical_degree IS NOT NULL
        )
    ),
    CONSTRAINT chk_nurse_fields CHECK (
        employee_type != 'NURSE' OR shift IS NOT NULL
    ),
    CONSTRAINT chk_admin_fields CHECK (
        employee_type != 'ADMINISTRATIVE_STAFF' OR department_area IS NOT NULL
    ),
    CONSTRAINT chk_receptionist_fields CHECK (
        employee_type != 'RECEPTIONIST' OR desk_number IS NOT NULL
    ),
    CONSTRAINT chk_observator_fields CHECK (
        employee_type != 'OBSERVATOR' OR assigned_area IS NOT NULL
    )
);

-- Add foreign key constraint for supervisor (self-referencing)
ALTER TABLE departments 
ADD CONSTRAINT fk_department_supervisor 
FOREIGN KEY (supervisor_id) REFERENCES employees(id);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_employee_type ON employees(employee_type);
CREATE INDEX IF NOT EXISTS idx_employee_department ON employees(department_id);
CREATE INDEX IF NOT EXISTS idx_employee_role ON employees(role);
CREATE INDEX IF NOT EXISTS idx_employee_active ON employees(is_active);
CREATE INDEX IF NOT EXISTS idx_employee_matricule ON employees(matricule);
CREATE INDEX IF NOT EXISTS idx_department_name ON departments(name);