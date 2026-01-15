-- V0001__create_schema_and_academic_year.sql
-- Creates academic_core schema and academic_year table

CREATE SCHEMA IF NOT EXISTS academic_core;

CREATE TABLE academic_core.academic_year (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL,
    name VARCHAR(128) NOT NULL,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_academic_year_code ON academic_core.academic_year(code);
