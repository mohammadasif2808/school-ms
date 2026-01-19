package com.school.academic.frontoffice.dto.postal;

import com.school.academic.frontoffice.enums.FrontOfficeStatus;
import com.school.academic.frontoffice.enums.PostalDirection;
import com.school.academic.frontoffice.enums.PostalType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for PostalRecord entity.
 */
public class PostalRecordResponse {

    private UUID id;
    private PostalDirection direction;
    private PostalType postalType;
    private String referenceNumber;
    private String fromTitle;
    private String toTitle;
    private String courierName;
    private LocalDate date;
    private String attachmentUrl;
    private String notes;
    private FrontOfficeStatus status;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;

    public PostalRecordResponse() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public FrontOfficeStatus getStatus() {
        return status;
    }

    public void setStatus(FrontOfficeStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
