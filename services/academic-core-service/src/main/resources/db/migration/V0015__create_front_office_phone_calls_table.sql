-- V0015: Create Front Office Phone Calls Table
-- Module: Front Office
-- Entity: PhoneCall

CREATE TABLE IF NOT EXISTS `academic_core`.`front_office_phone_calls` (
    `id` BINARY(16) NOT NULL,
    `school_id` BINARY(16) NOT NULL,
    `academic_year_id` BINARY(16) NOT NULL,
    `caller_name` VARCHAR(100) NOT NULL,
    `phone_number` VARCHAR(20) NOT NULL,
    `call_date` DATE NOT NULL,
    `call_type` VARCHAR(20) NOT NULL COMMENT 'INCOMING, OUTGOING',
    `call_duration` INT NULL COMMENT 'Duration in minutes',
    `description` VARCHAR(1000) NULL,
    `next_follow_up_date` DATE NULL,
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    `remarks` VARCHAR(500) NULL,
    `created_at` DATETIME NOT NULL,
    `created_by` VARCHAR(64) NULL,
    `updated_at` DATETIME NULL,
    `updated_by` VARCHAR(64) NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_phone_calls_school_year` (`school_id`, `academic_year_id`),
    INDEX `idx_phone_calls_date` (`call_date`),
    INDEX `idx_phone_calls_type` (`call_type`),
    INDEX `idx_phone_calls_follow_up` (`next_follow_up_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
