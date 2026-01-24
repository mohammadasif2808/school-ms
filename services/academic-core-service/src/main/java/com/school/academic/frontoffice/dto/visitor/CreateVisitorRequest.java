package com.school.academic.frontoffice.dto.visitor;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a new visitor.
 */
public class CreateVisitorRequest {

    @NotBlank(message = "Visitor name is required")
    @Size(min = 2, max = 100, message = "Visitor name must be between 2 and 100 characters")
    private String visitorName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Purpose is required")
    @Size(min = 2, max = 255, message = "Purpose must be between 2 and 255 characters")
    private String purpose;

    @NotNull(message = "Number of persons is required")
    @Min(value = 1, message = "At least 1 person is required")
    @Max(value = 100, message = "Number of persons cannot exceed 100")
    private Integer numberOfPersons = 1;

    @Size(max = 50, message = "ID proof type cannot exceed 50 characters")
    private String idProofType;

    @Size(max = 50, message = "ID proof number cannot exceed 50 characters")
    private String idProofNumber;

    @NotNull(message = "Check-in time is required")
    private LocalDateTime checkInTime;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public CreateVisitorRequest() {
    }

    // Getters and Setters
    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Integer getNumberOfPersons() {
        return numberOfPersons;
    }

    public void setNumberOfPersons(Integer numberOfPersons) {
        this.numberOfPersons = numberOfPersons;
    }

    public String getIdProofType() {
        return idProofType;
    }

    public void setIdProofType(String idProofType) {
        this.idProofType = idProofType;
    }

    public String getIdProofNumber() {
        return idProofNumber;
    }

    public void setIdProofNumber(String idProofNumber) {
        this.idProofNumber = idProofNumber;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
