package com.school.academic.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Staff entity - Represents employees/contractors (Teachers, Admins, Drivers).
 * This service manages institutional profile only. Payroll/HR is OUT OF SCOPE.
 */
@Entity
@Table(name = "staff", schema = "academic_core")
public class Staff extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 64)
    private String userId; // Optional link to identity-service

    @Column(name = "staff_code", unique = true, length = 64)
    private String staffCode;

    @Column(name = "first_name", length = 128)
    private String firstName;

    @Column(name = "last_name", length = 128)
    private String lastName;

    @Column(name = "gender", length = 16)
    private String gender; // Male, Female, Other

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "designation", length = 64)
    private String designation;

    @Column(name = "qualification", length = 128)
    private String qualification;

    @Column(name = "professional_qualification", length = 256)
    private String professionalQualification;

    @Column(name = "work_experience", length = 64)
    private String workExperience;

    @Column(name = "mobile", length = 32)
    private String mobile;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "aadhar_number", length = 16)
    private String aadharNumber;

    @Column(name = "blood_group", length = 8)
    private String bloodGroup;

    @Column(name = "marital_status", length = 32)
    private String maritalStatus;

    @Column(name = "father_name", length = 128)
    private String fatherName;

    @Column(name = "mother_name", length = 128)
    private String motherName;

    @Column(name = "staff_type", length = 64)
    private String staffType; // TEACHER, RECEPTIONIST, etc.

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    // Permanent Address
    @Column(name = "permanent_address", length = 512)
    private String permanentAddress;

    @Column(name = "permanent_city", length = 128)
    private String permanentCity;

    @Column(name = "permanent_state", length = 128)
    private String permanentState;

    @Column(name = "permanent_postal_code", length = 16)
    private String permanentPostalCode;

    // Current Address
    @Column(name = "current_address", length = 512)
    private String currentAddress;

    @Column(name = "current_city", length = 128)
    private String currentCity;

    @Column(name = "current_state", length = 128)
    private String currentState;

    @Column(name = "current_postal_code", length = 16)
    private String currentPostalCode;

    // Social Media
    @Column(name = "facebook_url", length = 256)
    private String facebookUrl;

    @Column(name = "twitter_url", length = 256)
    private String twitterUrl;

    @Column(name = "linkedin_url", length = 256)
    private String linkedinUrl;

    @Column(name = "instagram_url", length = 256)
    private String instagramUrl;

    // Notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // File paths for uploads
    @Column(name = "photo_url", length = 512)
    private String photoUrl;

    @Column(name = "resume_url", length = 512)
    private String resumeUrl;

    @Column(name = "status", length = 32)
    private String status = "ACTIVE";

    // Default constructor
    public Staff() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getProfessionalQualification() {
        return professionalQualification;
    }

    public void setProfessionalQualification(String professionalQualification) {
        this.professionalQualification = professionalQualification;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getPermanentCity() {
        return permanentCity;
    }

    public void setPermanentCity(String permanentCity) {
        this.permanentCity = permanentCity;
    }

    public String getPermanentState() {
        return permanentState;
    }

    public void setPermanentState(String permanentState) {
        this.permanentState = permanentState;
    }

    public String getPermanentPostalCode() {
        return permanentPostalCode;
    }

    public void setPermanentPostalCode(String permanentPostalCode) {
        this.permanentPostalCode = permanentPostalCode;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getCurrentPostalCode() {
        return currentPostalCode;
    }

    public void setCurrentPostalCode(String currentPostalCode) {
        this.currentPostalCode = currentPostalCode;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Helper method to get full name.
     */
    public String getFullName() {
        return firstName + (lastName != null ? " " + lastName : "");
    }
}
