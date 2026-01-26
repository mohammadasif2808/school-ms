-- V0021__add_staff_extended_fields.sql
-- Adds extended fields to staff table for complete staff profile

-- Personal Information
ALTER TABLE academic_core.staff ADD COLUMN gender VARCHAR(16) NULL AFTER last_name;
ALTER TABLE academic_core.staff ADD COLUMN date_of_birth DATE NULL AFTER gender;
ALTER TABLE academic_core.staff ADD COLUMN aadhar_number VARCHAR(16) NULL AFTER email;
ALTER TABLE academic_core.staff ADD COLUMN blood_group VARCHAR(8) NULL AFTER aadhar_number;
ALTER TABLE academic_core.staff ADD COLUMN marital_status VARCHAR(32) NULL AFTER blood_group;
ALTER TABLE academic_core.staff ADD COLUMN father_name VARCHAR(128) NULL AFTER marital_status;
ALTER TABLE academic_core.staff ADD COLUMN mother_name VARCHAR(128) NULL AFTER father_name;

-- Professional Details
ALTER TABLE academic_core.staff ADD COLUMN professional_qualification VARCHAR(256) NULL AFTER qualification;
ALTER TABLE academic_core.staff ADD COLUMN work_experience VARCHAR(64) NULL AFTER professional_qualification;

-- Permanent Address
ALTER TABLE academic_core.staff ADD COLUMN permanent_address VARCHAR(512) NULL AFTER joining_date;
ALTER TABLE academic_core.staff ADD COLUMN permanent_city VARCHAR(128) NULL AFTER permanent_address;
ALTER TABLE academic_core.staff ADD COLUMN permanent_state VARCHAR(128) NULL AFTER permanent_city;
ALTER TABLE academic_core.staff ADD COLUMN permanent_postal_code VARCHAR(16) NULL AFTER permanent_state;

-- Current Address
ALTER TABLE academic_core.staff ADD COLUMN current_address VARCHAR(512) NULL AFTER permanent_postal_code;
ALTER TABLE academic_core.staff ADD COLUMN current_city VARCHAR(128) NULL AFTER current_address;
ALTER TABLE academic_core.staff ADD COLUMN current_state VARCHAR(128) NULL AFTER current_city;
ALTER TABLE academic_core.staff ADD COLUMN current_postal_code VARCHAR(16) NULL AFTER current_state;

-- Social Media
ALTER TABLE academic_core.staff ADD COLUMN facebook_url VARCHAR(256) NULL AFTER current_postal_code;
ALTER TABLE academic_core.staff ADD COLUMN twitter_url VARCHAR(256) NULL AFTER facebook_url;
ALTER TABLE academic_core.staff ADD COLUMN linkedin_url VARCHAR(256) NULL AFTER twitter_url;
ALTER TABLE academic_core.staff ADD COLUMN instagram_url VARCHAR(256) NULL AFTER linkedin_url;

-- Notes
ALTER TABLE academic_core.staff ADD COLUMN notes TEXT NULL AFTER instagram_url;

-- File URLs
ALTER TABLE academic_core.staff ADD COLUMN photo_url VARCHAR(512) NULL AFTER notes;
ALTER TABLE academic_core.staff ADD COLUMN resume_url VARCHAR(512) NULL AFTER photo_url;
