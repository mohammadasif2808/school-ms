-- V0020: Remove school_id from Front Office Tables
-- This migration removes multi-school support as the system is designed
-- for single-school deployment.
-- Each school deployment gets its own isolated database.

-- ============================================
-- 1. Front Office Visitors
-- ============================================
-- Drop the composite index that includes school_id
DROP INDEX `idx_visitors_school_year` ON `academic_core`.`front_office_visitors`;

-- Create new index without school_id
CREATE INDEX `idx_visitors_academic_year` ON `academic_core`.`front_office_visitors` (`academic_year_id`);

-- Drop the school_id column
ALTER TABLE `academic_core`.`front_office_visitors` DROP COLUMN `school_id`;


-- ============================================
-- 2. Front Office Phone Calls
-- ============================================
-- Drop the composite index that includes school_id
DROP INDEX `idx_phone_calls_school_year` ON `academic_core`.`front_office_phone_calls`;

-- Create new index without school_id
CREATE INDEX `idx_phone_calls_academic_year` ON `academic_core`.`front_office_phone_calls` (`academic_year_id`);

-- Drop the school_id column
ALTER TABLE `academic_core`.`front_office_phone_calls` DROP COLUMN `school_id`;


-- ============================================
-- 3. Front Office Half Day Notices
-- ============================================
-- Drop the composite index that includes school_id
DROP INDEX `idx_half_day_school_year` ON `academic_core`.`front_office_half_day_notices`;

-- Create new index without school_id
CREATE INDEX `idx_half_day_academic_year` ON `academic_core`.`front_office_half_day_notices` (`academic_year_id`);

-- Drop the school_id column
ALTER TABLE `academic_core`.`front_office_half_day_notices` DROP COLUMN `school_id`;


-- ============================================
-- 4. Front Office Postal Records
-- ============================================
-- Drop the composite index that includes school_id
DROP INDEX `idx_postal_school_year` ON `academic_core`.`front_office_postal_records`;

-- Create new index without school_id
CREATE INDEX `idx_postal_academic_year` ON `academic_core`.`front_office_postal_records` (`academic_year_id`);

-- Drop the school_id column
ALTER TABLE `academic_core`.`front_office_postal_records` DROP COLUMN `school_id`;


-- ============================================
-- 5. Front Office Admission Enquiries
-- ============================================
-- Drop the composite index that includes school_id
DROP INDEX `idx_enquiry_school_year` ON `academic_core`.`front_office_admission_enquiries`;

-- Create new index without school_id
CREATE INDEX `idx_enquiry_academic_year` ON `academic_core`.`front_office_admission_enquiries` (`academic_year_id`);

-- Drop the school_id column
ALTER TABLE `academic_core`.`front_office_admission_enquiries` DROP COLUMN `school_id`;


-- ============================================
-- 6. Front Office Complaints
-- ============================================
-- Drop the composite index that includes school_id
DROP INDEX `idx_complaint_school_year` ON `academic_core`.`front_office_complaints`;

-- Create new index without school_id
CREATE INDEX `idx_complaint_academic_year` ON `academic_core`.`front_office_complaints` (`academic_year_id`);

-- Drop the school_id column
ALTER TABLE `academic_core`.`front_office_complaints` DROP COLUMN `school_id`;
