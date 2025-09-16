-- ============================================================
-- Schema for auth-service
-- ============================================================

CREATE DATABASE IF NOT EXISTS authdb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE authdb;

-- ============================================================
-- Users table
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(150) UNIQUE,
    external_id VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- ============================================================
-- User roles (ElementCollection of UserAccount.roles)
-- ============================================================

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,   -- store enum name like ADMIN, DOCTOR, etc.
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);
