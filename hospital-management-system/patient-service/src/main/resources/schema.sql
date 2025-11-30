-- Create the database
CREATE DATABASE IF NOT EXISTS patientdb;
USE patientdb;

-- Create the patients table
CREATE TABLE patients (
    id VARCHAR(50) PRIMARY KEY,                             -- CIN or auto-generated ID
    nom VARCHAR(255) NOT NULL,                               -- Patient name
    date_naissance DATE NOT NULL,                            -- Birth date
    email VARCHAR(255),                                      -- Email
    numero_telephone VARCHAR(50),                            -- Phone number
    adresse VARCHAR(255),                                     -- Address
    numero_securite_sociale VARCHAR(50),                    -- Social security number
    cin VARCHAR(50),                                         -- CIN for adults
    is_minor BOOLEAN,                                        -- Minor flag
    parent_cin VARCHAR(50),                                  -- CIN of parent/guardian
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,           -- Record creation timestamp
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Trigger to auto-generate id for minors and enforce CIN for adults
DELIMITER $$

CREATE TRIGGER trg_before_insert_patient
BEFORE INSERT ON patients
FOR EACH ROW
BEGIN
    DECLARE age INT;
    SET age = TIMESTAMPDIFF(YEAR, NEW.date_naissance, CURDATE());
    
    -- Determine if minor
    IF age < 18 THEN
        SET NEW.is_minor = TRUE;
        -- Generate UUID if id is null
        IF NEW.id IS NULL THEN
            SET NEW.id = UUID();
        END IF;
        -- CIN is optional for minors
        SET NEW.cin = NULL;
    ELSE
        SET NEW.is_minor = FALSE;
        -- CIN is required for adults
        IF NEW.cin IS NULL OR NEW.cin = '' THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CIN is required for adult patients';
        END IF;
    END IF;
END$$

DELIMITER ;

-- Optional: Indexes for faster queries
CREATE INDEX idx_patients_email ON patients(email);
CREATE INDEX idx_patients_cin ON patients(cin);
CREATE INDEX idx_patients_parent_cin ON patients(parent_cin);
CREATE INDEX idx_patients_is_minor ON patients(is_minor);

-- ============================================================
-- Doctor-Related Medical Tables
-- ============================================================

-- Prescriptions Table
CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    doctor_id BIGINT NOT NULL,
    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration_days INT NOT NULL,
    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_prescription_patient FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_prescription_patient (patient_id),
    INDEX idx_prescription_doctor (doctor_id),
    INDEX idx_prescription_active (is_active),
    INDEX idx_prescription_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Medical Records Table
CREATE TABLE IF NOT EXISTS medical_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    doctor_id BIGINT NOT NULL,
    diagnosis VARCHAR(500) NOT NULL,
    treatment_plan TEXT,
    notes TEXT,
    record_type ENUM('CONSULTATION', 'DIAGNOSIS', 'TREATMENT', 'FOLLOW_UP') DEFAULT 'CONSULTATION',
    visit_date DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_medical_record_patient FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_medical_record_patient (patient_id),
    INDEX idx_medical_record_doctor (doctor_id),
    INDEX idx_medical_record_visit_date (visit_date),
    INDEX idx_medical_record_type (record_type),
    INDEX idx_medical_record_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Test Catalog Table
CREATE TABLE IF NOT EXISTS test_catalogs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    normal_range VARCHAR(100),
    unit VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_test_catalog_category (category),
    INDEX idx_test_catalog_active (is_active),
    UNIQUE KEY uk_test_catalog_name (test_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Diagnostic Reports Table
CREATE TABLE IF NOT EXISTS diagnostic_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    doctor_id BIGINT NOT NULL,
    test_id BIGINT NOT NULL,
    result_value VARCHAR(255) NOT NULL,
    report_date DATETIME NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'ABNORMAL') DEFAULT 'PENDING',
    notes TEXT,
    attachment_url VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_diagnostic_patient FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_diagnostic_test FOREIGN KEY (test_id)
        REFERENCES test_catalogs(id) ON DELETE RESTRICT,
    INDEX idx_diagnostic_patient (patient_id),
    INDEX idx_diagnostic_doctor (doctor_id),
    INDEX idx_diagnostic_status (status),
    INDEX idx_diagnostic_report_date (report_date),
    INDEX idx_diagnostic_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Consultation Fees Table
CREATE TABLE IF NOT EXISTS consultation_fees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    specialization VARCHAR(255),
    fee_amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_consultation_fee_doctor (doctor_id),
    INDEX idx_consultation_fee_specialization (specialization),
    INDEX idx_consultation_fee_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Patient Insurance Table
CREATE TABLE IF NOT EXISTS patient_insurance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL UNIQUE,
    insurance_provider VARCHAR(255),
    policy_number VARCHAR(255),
    coverage_percentage INT DEFAULT 80,
    is_active BOOLEAN DEFAULT TRUE,
    expiry_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_insurance_patient FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_insurance_patient (patient_id),
    INDEX idx_insurance_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample test catalog data
INSERT INTO test_catalogs (test_name, description, category, normal_range, unit) VALUES
('Blood Glucose', 'Fasting blood glucose test', 'BLOOD_WORK', '70-100 mg/dL', 'mg/dL'),
('Hemoglobin A1C', 'Glycemic control test', 'BLOOD_WORK', '< 5.7%', '%'),
('Total Cholesterol', 'Lipid panel test', 'BLOOD_WORK', '< 200 mg/dL', 'mg/dL'),
('Creatinine', 'Kidney function test', 'BLOOD_WORK', '0.6-1.2 mg/dL', 'mg/dL'),
('Chest X-Ray', 'Thoracic imaging', 'IMAGING', 'Normal', 'Report'),
('CT Scan', 'Computed tomography', 'IMAGING', 'Normal', 'Report'),
('ECG', 'Electrocardiogram', 'CARDIOLOGY', 'Normal sinus rhythm', 'Report'),
('Complete Blood Count', 'Full blood panel', 'BLOOD_WORK', 'Within range', 'Report')
ON DUPLICATE KEY UPDATE test_name=test_name;
