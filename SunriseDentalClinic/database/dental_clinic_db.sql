-- =====================================================================
-- Sunrise Dental Clinic - Database Schema & Seed Data
-- Run this entire script in MySQL Workbench (or mysql CLI) BEFORE
-- deploying the web application.
-- =====================================================================

DROP DATABASE IF EXISTS dental_clinic_db;
CREATE DATABASE dental_clinic_db CHARACTER SET utf8mb4;
USE dental_clinic_db;

-- ---------------------------------------------------------------------
-- 1. Users (staff login accounts)
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,   -- SHA-256 hash (64 hex chars)
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STAFF'  -- ADMIN or STAFF
);

-- Default accounts:
--   username: admin  | password: admin123  (role ADMIN)
--   username: staff  | password: staff123  (role STAFF)
-- IMPORTANT: change these passwords after first login in a real deployment.
INSERT INTO users (username, password, full_name, role) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Clinic Administrator', 'ADMIN'),
('staff', '10176e7b7b24d317acfcf8d2064cfd2f24e154f7b5a96603077d5ef813d6a6b6', 'Front Desk Staff', 'STAFF');

-- ---------------------------------------------------------------------
-- 2. Dentists
-- ---------------------------------------------------------------------
CREATE TABLE dentists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100)
);

INSERT INTO dentists (name, specialization) VALUES
('Dr. Nimal Perera', 'General Dentistry'),
('Dr. Kumari Silva', 'Orthodontics'),
('Dr. Ashan Fernando', 'Oral Surgery'),
('Dr. Dilani Jayasinghe', 'Pediatric Dentistry'),
('Dr. Ruwan Bandara', 'Periodontics');

-- ---------------------------------------------------------------------
-- 3. Treatments and their standard cost (LKR)
-- ---------------------------------------------------------------------
CREATE TABLE treatments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    treatment_name VARCHAR(100) NOT NULL UNIQUE,
    cost DECIMAL(10,2) NOT NULL
);

INSERT INTO treatments (treatment_name, cost) VALUES
('Dental Checkup', 1000.00),
('Teeth Cleaning (Scaling)', 3500.00),
('Tooth Filling', 4500.00),
('Root Canal Treatment', 15000.00),
('Tooth Extraction', 3000.00),
('Teeth Whitening', 12000.00),
('Dental Crown', 20000.00),
('Braces Consultation', 2500.00),
('Wisdom Tooth Removal', 8000.00);

-- ---------------------------------------------------------------------
-- 4. Appointments
-- ---------------------------------------------------------------------
CREATE TABLE appointments (
    appointment_no VARCHAR(30) PRIMARY KEY,
    patient_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    dentist_name VARCHAR(100) NOT NULL,
    treatment_type VARCHAR(100) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, COMPLETED, CANCELLED
    created_at DATETIME NOT NULL,
    INDEX idx_dentist_datetime (dentist_name, appointment_date, appointment_time),
    INDEX idx_patient_name (patient_name)
);

-- ---------------------------------------------------------------------
-- 5. Bills
-- ---------------------------------------------------------------------
CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_no VARCHAR(30) NOT NULL,
    patient_name VARCHAR(100) NOT NULL,
    treatment_type VARCHAR(100) NOT NULL,
    treatment_cost DECIMAL(10,2) NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    bill_date DATETIME NOT NULL,
    FOREIGN KEY (appointment_no) REFERENCES appointments(appointment_no)
);

-- =====================================================================
-- End of script
-- =====================================================================
