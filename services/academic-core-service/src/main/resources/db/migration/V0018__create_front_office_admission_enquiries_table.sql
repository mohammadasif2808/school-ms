-- V0018: Create Front Office Admission Enquiries Table
-- Module: Front Office
-- Entity: AdmissionEnquiry

CREATE TABLE IF NOT EXISTS `academic_core`.`front_office_admission_enquiries` (
    `id` BINARY(16) NOT NULL,
    `school_id` BINARY(16) NOT NULL,
    `academic_year_id` BINARY(16) NOT NULL,
    `enquirer_name` VARCHAR(100) NOT NULL,
    `phone_number` VARCHAR(20) NOT NULL,
    `enquiry_type` VARCHAR(20) NOT NULL COMMENT 'PARENT, STUDENT, OTHER',
    `source` VARCHAR(20) NOT NULL COMMENT 'WEBSITE, WALK_IN, CALL, REFERRAL, OTHER',
    `enquiry_date` DATE NOT NULL,
    `description` VARCHAR(1000) NULL,
    `last_follow_up_date` DATE NULL,
    `next_follow_up_date` DATE NULL,
    `enquiry_status` VARCHAR(20) NOT NULL DEFAULT 'NEW' COMMENT 'NEW, FOLLOW_UP, CONVERTED, CLOSED',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    `remarks` VARCHAR(500) NULL,
    `created_at` DATETIME NOT NULL,
    `created_by` VARCHAR(64) NULL,
    `updated_at` DATETIME NULL,
    `updated_by` VARCHAR(64) NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_enquiry_school_year` (`school_id`, `academic_year_id`),
    INDEX `idx_enquiry_status` (`enquiry_status`),
    INDEX `idx_enquiry_source` (`source`),
    INDEX `idx_enquiry_date` (`enquiry_date`),
    INDEX `idx_enquiry_follow_up` (`next_follow_up_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
