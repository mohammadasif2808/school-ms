package com.school.academic.frontoffice.dto.enquiry;

import com.school.academic.frontoffice.enums.EnquiryStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating admission enquiry status.
 */
public class UpdateEnquiryStatusRequest {

    @NotNull(message = "Status is required")
    private EnquiryStatus status;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public UpdateEnquiryStatusRequest() {
    }

    // Getters and Setters
    public EnquiryStatus getStatus() {
        return status;
    }

    public void setStatus(EnquiryStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
