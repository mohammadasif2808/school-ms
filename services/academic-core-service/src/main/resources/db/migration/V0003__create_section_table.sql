-- V0003__create_section_table.sql
-- Creates section table

CREATE TABLE academic_core.section (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(16) NOT NULL,
    name VARCHAR(64),
    description TEXT,
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_section_code ON academic_core.section(code);
