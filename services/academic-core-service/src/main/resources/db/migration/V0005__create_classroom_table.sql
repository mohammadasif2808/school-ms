-- V0005__create_classroom_table.sql
-- Creates classroom table (physical room)

CREATE TABLE academic_core.classroom (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(32) NOT NULL,
    name VARCHAR(128),
    capacity INT,
    infra_type VARCHAR(32),
    building_block VARCHAR(64),
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_classroom_room_number ON academic_core.classroom(room_number);
