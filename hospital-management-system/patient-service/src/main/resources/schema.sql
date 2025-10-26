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
