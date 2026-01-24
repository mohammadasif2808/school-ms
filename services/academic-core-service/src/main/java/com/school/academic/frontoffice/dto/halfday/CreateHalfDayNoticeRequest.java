package com.school.academic.frontoffice.dto.halfday;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for creating a new half day notice.
 */
public class CreateHalfDayNoticeRequest {

    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Class ID is required")
    private UUID classId;

    @NotNull(message = "Section ID is required")
    private UUID sectionId;

    @NotNull(message = "Out time is required")
    private LocalDateTime outTime;

    @NotBlank(message = "Reason is required")
    @Size(min = 2, max = 500, message = "Reason must be between 2 and 500 characters")
    private String reason;

    @NotBlank(message = "Guardian name is required")
    @Size(min = 2, max = 100, message = "Guardian name must be between 2 and 100 characters")
    private String guardianName;

    @NotBlank(message = "Guardian phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String guardianPhone;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public CreateHalfDayNoticeRequest() {
    }

    // Getters and Setters
    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public UUID getClassId() {
        return classId;
    }

    public void setClassId(UUID classId) {
        this.classId = classId;
    }

    public UUID getSectionId() {
        return sectionId;
    }

    public void setSectionId(UUID sectionId) {
        this.sectionId = sectionId;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
