-- V0012__create_subject_assignment_table.sql
-- Creates subject_assignment table (subject to class mapping per year)

CREATE TABLE academic_core.subject_assignment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    section_id BIGINT NULL,
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sa_subject FOREIGN KEY (subject_id) REFERENCES academic_core.subject(id),
    CONSTRAINT fk_sa_class FOREIGN KEY (class_id) REFERENCES academic_core.class(id),
    CONSTRAINT fk_sa_year FOREIGN KEY (academic_year_id) REFERENCES academic_core.academic_year(id),
    CONSTRAINT fk_sa_section FOREIGN KEY (section_id) REFERENCES academic_core.section(id),
    UNIQUE KEY ux_sa_class_subject_year_section (subject_id, class_id, academic_year_id, section_id)
);
