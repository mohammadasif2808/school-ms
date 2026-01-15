-- V0011__create_enrollment_table.sql
-- Creates enrollment table (student placement in ClassSection)

CREATE TABLE academic_core.enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    class_section_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    roll_number INT NULL,
    enrollment_status VARCHAR(32) DEFAULT 'ACTIVE',
    enrollment_date DATE,
    end_date DATE,
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(64),
    modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_enr_student FOREIGN KEY (student_id) REFERENCES academic_core.student(id),
    CONSTRAINT fk_enr_class_section FOREIGN KEY (class_section_id) REFERENCES academic_core.class_section(id),
    CONSTRAINT fk_enr_year FOREIGN KEY (academic_year_id) REFERENCES academic_core.academic_year(id)
);

-- Roll number unique per (class_section_id, academic_year_id)
CREATE UNIQUE INDEX ux_enrollment_roll ON academic_core.enrollment (class_section_id, academic_year_id, roll_number);

-- Student can have only one enrollment per academic year
CREATE UNIQUE INDEX ux_enrollment_student_year ON academic_core.enrollment (student_id, academic_year_id);
