-- Insert departments first (without supervisors)
INSERT INTO departments (id, name) VALUES 
(1, 'Administration'),
(2, 'Cardiology'),
(3, 'Emergency'),
(4, 'Pediatrics'),
(5, 'Reception'),
(6, 'HR'),
(7, 'Logistics'),
(8, 'Observation');

-- Insert employees
INSERT INTO employees (
    employee_type, matricule, first_name, last_name, email, phone, role, 
    department_id, is_active, hire_date, address,
    -- Doctor fields
    specialization, license_number, medical_degree,
    -- Nurse fields  
    shift, nursing_license,
    -- Admin staff fields
    department_area,
    -- Receptionist fields
    desk_number,
    -- Observator fields
    assigned_area
) VALUES 

-- Doctors
('DOCTOR', 'DOC001', 'John', 'Smith', 'john.smith@hospital.com', '+1234567890', 'DOCTOR', 
 2, true, '2020-01-15', '123 Medical Street', 
 'Cardiology', 'LIC123456', 'MD Cardiology', 
 NULL, NULL, NULL, NULL, NULL),

('DOCTOR', 'DOC002', 'Sarah', 'Johnson', 'sarah.johnson@hospital.com', '+1234567891', 'DOCTOR', 
 3, true, '2019-03-10', '456 Healthcare Ave', 
 'Emergency Medicine', 'LIC789012', 'MD Emergency Medicine', 
 NULL, NULL, NULL, NULL, NULL),

('DOCTOR', 'DOC003', 'Michael', 'Brown', 'michael.brown@hospital.com', '+1234567892', 'DOCTOR', 
 4, true, '2021-06-20', '789 Pediatric Lane', 
 'Pediatrics', 'LIC345678', 'MD Pediatrics', 
 NULL, NULL, NULL, NULL, NULL),

-- Nurses
('NURSE', 'NUR001', 'Emily', 'Davis', 'emily.davis@hospital.com', '+1234567893', 'NURSE', 
 2, true, '2020-05-15', '321 Nursing Street', 
 NULL, NULL, NULL, 
 'DAY', 'NL987654', NULL, NULL, NULL),

('NURSE', 'NUR002', 'James', 'Wilson', 'james.wilson@hospital.com', '+1234567894', 'NURSE', 
 3, true, '2019-11-08', '654 Care Avenue', 
 NULL, NULL, NULL, 
 'NIGHT', 'NL876543', NULL, NULL, NULL),

('NURSE', 'NUR003', 'Lisa', 'Martinez', 'lisa.martinez@hospital.com', '+1234567895', 'NURSE', 
 4, true, '2021-02-14', '987 Pediatric Care St', 
 NULL, NULL, NULL, 
 'ROTATING', 'NL765432', NULL, NULL, NULL),

-- Administrative Staff
('ADMINISTRATIVE_STAFF', 'ADM001', 'Robert', 'Taylor', 'robert.taylor@hospital.com', '+1234567896', 'HR', 
 6, true, '2018-07-01', '111 Admin Building', 
 NULL, NULL, NULL, NULL, NULL, 
 'HR', NULL, NULL),

('ADMINISTRATIVE_STAFF', 'ADM002', 'Jennifer', 'Anderson', 'jennifer.anderson@hospital.com', '+1234567897', 'LOGISTICS', 
 7, true, '2019-09-12', '222 Supply Chain St', 
 NULL, NULL, NULL, NULL, NULL, 
 'Logistics', NULL, NULL),

('ADMINISTRATIVE_STAFF', 'ADM003', 'David', 'Thomas', 'david.thomas@hospital.com', '+1234567898', 'HR', 
 6, true, '2020-12-03', '333 HR Department', 
 NULL, NULL, NULL, NULL, NULL, 
 'Materials', NULL, NULL),

-- Receptionists
('RECEPTIONIST', 'REC001', 'Maria', 'Garcia', 'maria.garcia@hospital.com', '+1234567899', 'RECEPTION', 
 5, true, '2020-08-17', '444 Front Desk Area', 
 NULL, NULL, NULL, NULL, NULL, NULL, 
 'DESK-01', NULL),

('RECEPTIONIST', 'REC002', 'Kevin', 'Lee', 'kevin.lee@hospital.com', '+1234567800', 'RECEPTION', 
 5, true, '2021-01-25', '555 Reception Hall', 
 NULL, NULL, NULL, NULL, NULL, NULL, 
 'DESK-02', NULL),

-- Observators
('OBSERVATOR', 'OBS001', 'Rachel', 'White', 'rachel.white@hospital.com', '+1234567801', 'OBSERVATOR', 
 8, true, '2021-04-10', '666 Observation Wing', 
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
 'ICU-MONITORING'),

('OBSERVATOR', 'OBS002', 'Thomas', 'Harris', 'thomas.harris@hospital.com', '+1234567802', 'OBSERVATOR', 
 8, true, '2020-10-15', '777 Watch Tower', 
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
 'EMERGENCY-MONITORING');

-- Update departments with supervisors (now that employees exist)
UPDATE departments SET supervisor_id = 1 WHERE name = 'Cardiology';      -- Dr. John Smith
UPDATE departments SET supervisor_id = 2 WHERE name = 'Emergency';       -- Dr. Sarah Johnson  
UPDATE departments SET supervisor_id = 3 WHERE name = 'Pediatrics';      -- Dr. Michael Brown
UPDATE departments SET supervisor_id = 7 WHERE name = 'HR';              -- Robert Taylor
UPDATE departments SET supervisor_id = 8 WHERE name = 'Logistics';       -- Jennifer Anderson
UPDATE departments SET supervisor_id = 10 WHERE name = 'Reception';      -- Maria Garcia
UPDATE departments SET supervisor_id = 12 WHERE name = 'Observation';    -- Rachel White