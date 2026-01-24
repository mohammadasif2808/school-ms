package com.school.academic.frontoffice.dto.enquiry;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request DTO for updating admission enquiry follow-up dates.
 */
public class UpdateEnquiryFollowUpRequest {

    private LocalDate lastFollowUpDate;

    private LocalDate nextFollowUpDate;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public UpdateEnquiryFollowUpRequest() {
    }

    // Getters and Setters
    public LocalDate getLastFollowUpDate() {
        return lastFollowUpDate;
    }

    public void setLastFollowUpDate(LocalDate lastFollowUpDate) {
        this.lastFollowUpDate = lastFollowUpDate;
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
