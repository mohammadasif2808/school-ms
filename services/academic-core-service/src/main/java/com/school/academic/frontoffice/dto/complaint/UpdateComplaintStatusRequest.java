package com.school.academic.frontoffice.dto.complaint;

import com.school.academic.frontoffice.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating complaint status.
 */
public class UpdateComplaintStatusRequest {

    @NotNull(message = "Status is required")
    private ComplaintStatus status;

    @Size(max = 2000, message = "Action taken cannot exceed 2000 characters")
    private String actionTaken;

    @Size(max = 1000, message = "Internal note cannot exceed 1000 characters")
    private String internalNote;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public UpdateComplaintStatusRequest() {
    }

    // Getters and Setters
    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getInternalNote() {
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
