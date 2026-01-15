package com.school.academic.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Student entity - The cornerstone entity. Represents the individual, independent of time.
 */
@Entity
@Table(name = "student", schema = "academic_core")
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 64)
    private String userId; // Optional link to identity-service

    @Column(name = "admission_no", nullable = false, unique = true, length = 64)
    private String admissionNumber;

    @Column(name = "first_name", nullable = false, length = 128)
    private String firstName;

    @Column(name = "last_name", length = 128)
    private String lastName;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "gender", length = 16)
    private String gender; // Male, Female, Other

    @Column(name = "blood_group", length = 8)
    private String bloodGroup;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "status", length = 32)
    private String status = "ACTIVE"; // Active, Alumni, Withdrawn

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentParent> guardians = new HashSet<>();

    // Default constructor
    public Student() {
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

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
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

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Set<StudentParent> getGuardians() {
        return guardians;
    }

    public void setGuardians(Set<StudentParent> guardians) {
        this.guardians = guardians;
    }

    /**
     * Helper method to get full name.
     */
    public String getFullName() {
        return firstName + (lastName != null ? " " + lastName : "");
    }
}
