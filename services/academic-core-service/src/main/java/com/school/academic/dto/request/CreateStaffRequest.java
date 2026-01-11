package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateStaffRequest {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Designation is required")
    private String designation;

    private String qualification;
    private String mobile;
    private String email;

    public CreateStaffRequest() {
    }

    public CreateStaffRequest(String employeeId, String userId, String firstName, String lastName,
                             String designation, String qualification, String mobile, String email) {
        this.employeeId = employeeId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.designation = designation;
        this.qualification = qualification;
        this.mobile = mobile;
        this.email = email;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}

