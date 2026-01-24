-- V0004__create_subject_table.sql
-- Creates subject table

CREATE TABLE academic_core.subject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64),
    name VARCHAR(128) NOT NULL,
    type VARCHAR(32),
    is_optional BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_subject_code ON academic_core.subject(code);
