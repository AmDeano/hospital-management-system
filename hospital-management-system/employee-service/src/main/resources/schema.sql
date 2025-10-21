-- ============================================================
-- Schema for auth-service
-- ============================================================
CREATE DATABASE IF NOT EXISTS authdb
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE authdb;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricule VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    external_id VARCHAR(255),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_user_matricule UNIQUE (matricule),
    CONSTRAINT uk_user_email UNIQUE (email),
    CONSTRAINT chk_matricule_not_empty CHECK (CHAR_LENGTH(TRIM(matricule)) > 0),
    CONSTRAINT chk_password_not_empty CHECK (CHAR_LENGTH(TRIM(password_hash)) > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create user_roles table (many-to-many relationship)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_valid_role CHECK (
        role IN (
            'ADMIN', 'HR', 'DOCTOR', 'NURSE', 
            'RECEPTIONIST', 'OBSERVATOR', 'SUPERVISOR', 'PATIENT'
        )
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better performance
CREATE INDEX idx_user_matricule ON users(matricule);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_enabled ON users(enabled);
CREATE INDEX idx_user_external_id ON users(external_id);
CREATE INDEX idx_user_created_at ON users(created_at);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role);

-- Create audit table for tracking authentication events (optional but recommended)
CREATE TABLE IF NOT EXISTS auth_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    matricule VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL, -- LOGIN_SUCCESS, LOGIN_FAILURE, PASSWORD_CHANGE, etc.
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT,
    
    INDEX idx_audit_user_id (user_id),
    INDEX idx_audit_matricule (matricule),
    INDEX idx_audit_event_type (event_type),
    INDEX idx_audit_timestamp (event_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create refresh tokens table for tracking active sessions
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP NULL,
    
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_refresh_token_user (user_id),
    INDEX idx_refresh_token_expires (expires_at),
    INDEX idx_refresh_token_revoked (revoked)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default admin user (password: admin123 - CHANGE THIS IN PRODUCTION!)
-- BCrypt hash for "admin123": $2a$10$XdDXvPVTdLqDXqS2JL0D0eJmKmJJhqLh6XQUBNdZHY4g8JG9pFZmS
INSERT INTO users (matricule, password_hash, email, first_name, last_name, enabled, external_id)
VALUES (
    'ADMIN001',
    '$2a$10$XdDXvPVTdLqDXqS2JL0D0eJmKmJJhqLh6XQUBNdZHY4g8JG9pFZmS',
    'admin@hospital.com',
    'System',
    'Administrator',
    TRUE,
    'ADMIN001'
) ON DUPLICATE KEY UPDATE matricule=matricule;

INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE matricule = 'ADMIN001'
ON DUPLICATE KEY UPDATE role=role;

-- Create view for user details with roles (for easier querying)
CREATE OR REPLACE VIEW v_users_with_roles AS
SELECT 
    u.id,
    u.matricule,
    u.email,
    u.first_name,
    u.last_name,
    u.external_id,
    u.enabled,
    u.created_at,
    u.updated_at,
    GROUP_CONCAT(ur.role ORDER BY ur.role SEPARATOR ',') as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
GROUP BY u.id, u.matricule, u.email, u.first_name, u.last_name, 
         u.external_id, u.enabled, u.created_at, u.updated_at;