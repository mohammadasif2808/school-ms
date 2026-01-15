-- V0002__create_class_table.sql
-- Creates class table (grade level)

CREATE TABLE academic_core.class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL,
    name VARCHAR(128) NOT NULL,
    level_order INT,
    description TEXT,
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_class_code ON academic_core.class(code);
