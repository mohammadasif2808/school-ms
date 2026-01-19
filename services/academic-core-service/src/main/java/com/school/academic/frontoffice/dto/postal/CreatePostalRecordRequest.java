package com.school.academic.frontoffice.dto.postal;

import com.school.academic.frontoffice.enums.PostalDirection;
import com.school.academic.frontoffice.enums.PostalType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Request DTO for creating a new postal record.
 */
public class CreatePostalRecordRequest {

    @NotNull(message = "Direction is required")
    private PostalDirection direction;

    @NotNull(message = "Postal type is required")
    private PostalType postalType;

    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    private String referenceNumber;

    @NotBlank(message = "From title is required")
    @Size(min = 2, max = 200, message = "From title must be between 2 and 200 characters")
    private String fromTitle;

    @NotBlank(message = "To title is required")
    @Size(min = 2, max = 200, message = "To title must be between 2 and 200 characters")
    private String toTitle;

    @Size(max = 100, message = "Courier name cannot exceed 100 characters")
    private String courierName;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Size(max = 500, message = "Attachment URL cannot exceed 500 characters")
    @org.hibernate.validator.constraints.URL(message = "Attachment URL must be a valid URL")
    private String attachmentUrl;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public CreatePostalRecordRequest() {
    }

    // Getters and Setters
    public PostalDirection getDirection() {
        return direction;
    }

    public void setDirection(PostalDirection direction) {
        this.direction = direction;
    }

    public PostalType getPostalType() {
        return postalType;
    }

    public void setPostalType(PostalType postalType) {
        this.postalType = postalType;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getFromTitle() {
        return fromTitle;
    }

    public void setFromTitle(String fromTitle) {
        this.fromTitle = fromTitle;
    }

    public String getToTitle() {
        return toTitle;
    }

    public void setToTitle(String toTitle) {
        this.toTitle = toTitle;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
