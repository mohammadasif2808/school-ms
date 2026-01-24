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

    @Column(name = "designation", length = 64)
    private String designation;

    @Column(name = "qualification", length = 128)
    private String qualification;

    @Column(name = "mobile", length = 32)
    private String mobile;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "staff_type", length = 64)
    private String staffType; // TEACHER, RECEPTIONIST, etc.

    @Column(name = "joining_date")
    private LocalDate joiningDate;

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
