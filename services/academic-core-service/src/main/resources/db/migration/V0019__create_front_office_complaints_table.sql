-- V0019: Create Front Office Complaints Table
-- Module: Front Office
-- Entity: Complaint

CREATE TABLE IF NOT EXISTS `academic_core`.`front_office_complaints` (
    `id` BINARY(16) NOT NULL,
    `school_id` BINARY(16) NOT NULL,
    `academic_year_id` BINARY(16) NOT NULL,
    `complainant_name` VARCHAR(100) NOT NULL,
    `complaint_type` VARCHAR(20) NOT NULL COMMENT 'PARENT, STUDENT, STAFF',
    `category` VARCHAR(100) NOT NULL,
    `complaint_date` DATE NOT NULL,
    `description` VARCHAR(2000) NOT NULL,
    `action_taken` VARCHAR(2000) NULL,
    `assigned_to_staff_id` BIGINT NULL,
    `assigned_to_staff_name` VARCHAR(200) NULL COMMENT 'Denormalized for display',
    `internal_note` VARCHAR(1000) NULL,
    `complaint_status` VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN, IN_PROGRESS, RESOLVED, CLOSED',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    `remarks` VARCHAR(500) NULL,
    `created_at` DATETIME NOT NULL,
    `created_by` VARCHAR(64) NULL,
    `updated_at` DATETIME NULL,
    `updated_by` VARCHAR(64) NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_complaint_school_year` (`school_id`, `academic_year_id`),
    INDEX `idx_complaint_status` (`complaint_status`),
    INDEX `idx_complaint_type` (`complaint_type`),
    INDEX `idx_complaint_date` (`complaint_date`),
    INDEX `idx_complaint_assigned` (`assigned_to_staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
