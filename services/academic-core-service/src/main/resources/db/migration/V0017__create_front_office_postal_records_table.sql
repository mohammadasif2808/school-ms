-- V0017: Create Front Office Postal Records Table
-- Module: Front Office
-- Entity: PostalRecord

CREATE TABLE IF NOT EXISTS `academic_core`.`front_office_postal_records` (
    `id` BINARY(16) NOT NULL,
    `school_id` BINARY(16) NOT NULL,
    `academic_year_id` BINARY(16) NOT NULL,
    `direction` VARCHAR(20) NOT NULL COMMENT 'RECEIVED, DISPATCHED',
    `postal_type` VARCHAR(20) NOT NULL COMMENT 'LETTER, PARCEL, COURIER',
    `reference_number` VARCHAR(100) NULL,
    `from_title` VARCHAR(200) NOT NULL,
    `to_title` VARCHAR(200) NOT NULL,
    `courier_name` VARCHAR(100) NULL,
    `postal_date` DATE NOT NULL,
    `attachment_url` VARCHAR(500) NULL,
    `notes` VARCHAR(1000) NULL,
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    `remarks` VARCHAR(500) NULL,
    `created_at` DATETIME NOT NULL,
    `created_by` VARCHAR(64) NULL,
    `updated_at` DATETIME NULL,
    `updated_by` VARCHAR(64) NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_postal_school_year` (`school_id`, `academic_year_id`),
    INDEX `idx_postal_direction` (`direction`),
    INDEX `idx_postal_type` (`postal_type`),
    INDEX `idx_postal_date` (`postal_date`),
    INDEX `idx_postal_reference` (`reference_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
