-- V0014: Create Front Office Visitors Table
-- Module: Front Office
-- Entity: Visitor

CREATE TABLE IF NOT EXISTS `academic_core`.`front_office_visitors` (
    `id` BINARY(16) NOT NULL,
    `school_id` BINARY(16) NOT NULL,
    `academic_year_id` BINARY(16) NOT NULL,
    `visitor_name` VARCHAR(100) NOT NULL,
    `phone_number` VARCHAR(20) NOT NULL,
    `purpose` VARCHAR(255) NOT NULL,
    `number_of_persons` INT NOT NULL DEFAULT 1,
    `id_proof_type` VARCHAR(50) NULL,
    `id_proof_number` VARCHAR(50) NULL,
    `check_in_time` DATETIME NOT NULL,
    `check_out_time` DATETIME NULL,
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    `remarks` VARCHAR(500) NULL,
    `created_at` DATETIME NOT NULL,
    `created_by` VARCHAR(64) NULL,
    `updated_at` DATETIME NULL,
    `updated_by` VARCHAR(64) NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_visitors_school_year` (`school_id`, `academic_year_id`),
    INDEX `idx_visitors_check_in` (`check_in_time`),
    INDEX `idx_visitors_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
