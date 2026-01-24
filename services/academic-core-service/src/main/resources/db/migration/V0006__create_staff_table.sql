-- V0006__create_staff_table.sql
-- Creates staff table (institutional profile)

CREATE TABLE academic_core.staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NULL,
    staff_code VARCHAR(64),
    first_name VARCHAR(128),
    last_name VARCHAR(128),
    designation VARCHAR(64),
    qualification VARCHAR(128),
    mobile VARCHAR(32),
    email VARCHAR(128),
    staff_type VARCHAR(64),
    joining_date DATE,
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_staff_staff_code ON academic_core.staff(staff_code);
