-- V0010__create_class_section_table.sql
-- Creates class_section table (intersection of Class, Section, AcademicYear)

CREATE TABLE academic_core.class_section (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    section_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    medium VARCHAR(32) DEFAULT 'English',
    class_teacher_staff_id BIGINT NULL,
    classroom_id BIGINT NULL,
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cs_class FOREIGN KEY (class_id) REFERENCES academic_core.class(id),
    CONSTRAINT fk_cs_section FOREIGN KEY (section_id) REFERENCES academic_core.section(id),
    CONSTRAINT fk_cs_year FOREIGN KEY (academic_year_id) REFERENCES academic_core.academic_year(id),
    CONSTRAINT fk_cs_teacher FOREIGN KEY (class_teacher_staff_id) REFERENCES academic_core.staff(id),
    CONSTRAINT fk_cs_classroom FOREIGN KEY (classroom_id) REFERENCES academic_core.classroom(id),
    UNIQUE KEY ux_class_section_year (class_id, section_id, academic_year_id)
);
