package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateParentRequest {

    private String userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    private String email;

    @NotNull(message = "Relationship is required")
    private String relationship;

    private String address;

    public CreateParentRequest() {
    }

    public CreateParentRequest(String userId, String firstName, String lastName, String mobile,
                              String email, String relationship, String address) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
        this.relationship = relationship;
        this.address = address;
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

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

