package com.school.academic.frontoffice.dto.complaint;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for assigning a complaint to staff.
 */
public class AssignComplaintRequest {

    @NotNull(message = "Assigned staff ID is required")
    private UUID assignedToStaffId;

    @Size(max = 1000, message = "Internal note cannot exceed 1000 characters")
    private String internalNote;

    public AssignComplaintRequest() {
    }

    // Getters and Setters
    public UUID getAssignedToStaffId() {
        return assignedToStaffId;
    }

    public void setAssignedToStaffId(UUID assignedToStaffId) {
        this.assignedToStaffId = assignedToStaffId;
    }

    public String getInternalNote() {
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }
}
