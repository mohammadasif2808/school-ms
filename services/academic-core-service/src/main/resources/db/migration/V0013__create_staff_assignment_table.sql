-- V0013__create_staff_assignment_table.sql
-- Creates staff_assignment table (staff teaching subject in class_section)

CREATE TABLE academic_core.staff_assignment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    class_section_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sta_staff FOREIGN KEY (staff_id) REFERENCES academic_core.staff(id),
    CONSTRAINT fk_sta_subject FOREIGN KEY (subject_id) REFERENCES academic_core.subject(id),
    CONSTRAINT fk_sta_class_section FOREIGN KEY (class_section_id) REFERENCES academic_core.class_section(id),
    CONSTRAINT fk_sta_year FOREIGN KEY (academic_year_id) REFERENCES academic_core.academic_year(id),
    UNIQUE KEY ux_staff_subject_section_year (staff_id, subject_id, class_section_id, academic_year_id)
);
