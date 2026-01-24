package com.school.academic.frontoffice.dto.enquiry;

import com.school.academic.frontoffice.enums.EnquirySource;
import com.school.academic.frontoffice.enums.EnquiryType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Request DTO for creating a new admission enquiry.
 */
public class CreateAdmissionEnquiryRequest {

    @NotBlank(message = "Enquirer name is required")
    @Size(min = 2, max = 100, message = "Enquirer name must be between 2 and 100 characters")
    private String enquirerName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Enquiry type is required")
    private EnquiryType enquiryType;

    @NotNull(message = "Source is required")
    private EnquirySource source;

    @NotNull(message = "Enquiry date is required")
    private LocalDate enquiryDate;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private LocalDate nextFollowUpDate;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public CreateAdmissionEnquiryRequest() {
    }

    // Getters and Setters
    public String getEnquirerName() {
        return enquirerName;
    }

    public void setEnquirerName(String enquirerName) {
        this.enquirerName = enquirerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public EnquiryType getEnquiryType() {
        return enquiryType;
    }

    public void setEnquiryType(EnquiryType enquiryType) {
        this.enquiryType = enquiryType;
    }

    public EnquirySource getSource() {
        return source;
    }

    public void setSource(EnquirySource source) {
        this.source = source;
    }

    public LocalDate getEnquiryDate() {
        return enquiryDate;
    }

    public void setEnquiryDate(LocalDate enquiryDate) {
        this.enquiryDate = enquiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getNextFollowUpDate() {
        return nextFollowUpDate;
    }

    public void setNextFollowUpDate(LocalDate nextFollowUpDate) {
        this.nextFollowUpDate = nextFollowUpDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
