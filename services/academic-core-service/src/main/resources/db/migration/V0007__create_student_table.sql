-- V0007__create_student_table.sql
-- Creates student table (academic profile)

CREATE TABLE academic_core.student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NULL,
    admission_no VARCHAR(64) NOT NULL,
    first_name VARCHAR(128) NOT NULL,
    last_name VARCHAR(128),
    dob DATE,
    gender VARCHAR(16),
    blood_group VARCHAR(8),
    address TEXT,
    joining_date DATE,
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_student_admission_no ON academic_core.student(admission_no);
