package com.school.academic.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public class StudentResponse {

    private UUID id;
    private String admissionNumber;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private LocalDate joiningDate;
    private String status;
    private String userId;

    public StudentResponse() {
    }

    public StudentResponse(UUID id, String admissionNumber, String firstName, String lastName,
                          LocalDate dob, String gender, LocalDate joiningDate, String status, String userId) {
        this.id = id;
        this.admissionNumber = admissionNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.joiningDate = joiningDate;
        this.status = status;
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

