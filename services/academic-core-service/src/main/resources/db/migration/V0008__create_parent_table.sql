-- V0008__create_parent_table.sql
-- Creates parent/guardian table

CREATE TABLE academic_core.parent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NULL,
    first_name VARCHAR(128) NOT NULL,
    last_name VARCHAR(128),
    phone VARCHAR(32),
    email VARCHAR(128),
    relationship VARCHAR(64),
    address TEXT,
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);
