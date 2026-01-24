package com.school.academic.frontoffice.dto.phonecall;

import com.school.academic.frontoffice.enums.CallType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Request DTO for creating a new phone call log.
 */
public class CreatePhoneCallRequest {

    @NotBlank(message = "Caller name is required")
    @Size(min = 2, max = 100, message = "Caller name must be between 2 and 100 characters")
    private String callerName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Call date is required")
    private LocalDate callDate;

    @NotNull(message = "Call type is required")
    private CallType callType;

    @Min(value = 0, message = "Call duration cannot be negative")
    @Max(value = 480, message = "Call duration cannot exceed 480 minutes (8 hours)")
    private Integer callDuration;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private LocalDate nextFollowUpDate;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public CreatePhoneCallRequest() {
    }

    // Getters and Setters
    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getCallDate() {
        return callDate;
    }

    public void setCallDate(LocalDate callDate) {
        this.callDate = callDate;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public Integer getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(Integer callDuration) {
        this.callDuration = callDuration;
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
