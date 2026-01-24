package com.school.academic.frontoffice.entity;

import com.school.academic.frontoffice.enums.PostalDirection;
import com.school.academic.frontoffice.enums.PostalType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity for tracking incoming and outgoing physical mail and parcels.
 */
@Entity
@Table(name = "front_office_postal_records", schema = "academic_core")
public class PostalRecord extends FrontOfficeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private PostalDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "postal_type", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private PostalType postalType;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "from_title", nullable = false, length = 200)
    private String fromTitle;

    @Column(name = "to_title", nullable = false, length = 200)
    private String toTitle;

    @Column(name = "courier_name", length = 100)
    private String courierName;

    @Column(name = "postal_date", nullable = false)
    private LocalDate date;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Column(name = "notes", length = 1000)
    private String notes;

    // Constructors
    public PostalRecord() {
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
}
