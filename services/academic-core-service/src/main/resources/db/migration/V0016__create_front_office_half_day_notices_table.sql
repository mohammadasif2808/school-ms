-- V0016: Create Front Office Half Day Notices Table
-- Module: Front Office
-- Entity: HalfDayNotice

CREATE TABLE IF NOT EXISTS `academic_core`.`front_office_half_day_notices` (
    `id` BINARY(16) NOT NULL,
    `school_id` BINARY(16) NOT NULL,
    `academic_year_id` BINARY(16) NOT NULL,
    `student_id` BIGINT NOT NULL,
    `class_id` BIGINT NOT NULL,
    `section_id` BIGINT NOT NULL,
    `out_time` DATETIME NOT NULL,
    `reason` VARCHAR(500) NOT NULL,
    `guardian_name` VARCHAR(100) NOT NULL,
    `guardian_phone` VARCHAR(20) NOT NULL,
    `student_name` VARCHAR(200) NULL COMMENT 'Denormalized for display',
    `class_name` VARCHAR(100) NULL COMMENT 'Denormalized for display',
    `section_name` VARCHAR(100) NULL COMMENT 'Denormalized for display',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    `remarks` VARCHAR(500) NULL,
    `created_at` DATETIME NOT NULL,
    `created_by` VARCHAR(64) NULL,
    `updated_at` DATETIME NULL,
    `updated_by` VARCHAR(64) NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_half_day_school_year` (`school_id`, `academic_year_id`),
    INDEX `idx_half_day_student` (`student_id`),
    INDEX `idx_half_day_class_section` (`class_id`, `section_id`),
    INDEX `idx_half_day_out_time` (`out_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
