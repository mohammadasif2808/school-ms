-- V0009__create_student_parent_table.sql
-- Creates student_parent link table (N:M relationship)

CREATE TABLE academic_core.student_parent (
    student_id BIGINT NOT NULL,
    parent_id BIGINT NOT NULL,
    relationship VARCHAR(64),
    is_primary_contact BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id, parent_id),
    CONSTRAINT fk_sp_student FOREIGN KEY (student_id) REFERENCES academic_core.student(id) ON DELETE CASCADE,
    CONSTRAINT fk_sp_parent FOREIGN KEY (parent_id) REFERENCES academic_core.parent(id) ON DELETE CASCADE
);
