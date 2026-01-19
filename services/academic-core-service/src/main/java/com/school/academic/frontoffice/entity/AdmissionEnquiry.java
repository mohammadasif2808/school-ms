package com.school.academic.frontoffice.entity;

import com.school.academic.frontoffice.enums.EnquirySource;
import com.school.academic.frontoffice.enums.EnquiryStatus;
import com.school.academic.frontoffice.enums.EnquiryType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity for managing prospective student admission enquiries.
 */
@Entity
@Table(name = "front_office_admission_enquiries", schema = "academic_core")
public class AdmissionEnquiry extends FrontOfficeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "enquirer_name", nullable = false, length = 100)
    private String enquirerName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "enquiry_type", nullable = false, length = 20)
    private EnquiryType enquiryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private EnquirySource source;

    @Column(name = "enquiry_date", nullable = false)
    private LocalDate enquiryDate;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "last_follow_up_date")
    private LocalDate lastFollowUpDate;

    @Column(name = "next_follow_up_date")
    private LocalDate nextFollowUpDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "enquiry_status", nullable = false, length = 20)
    private EnquiryStatus enquiryStatus = EnquiryStatus.NEW;

    // Constructors
    public AdmissionEnquiry() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public EnquiryStatus getEnquiryStatus() {
        return enquiryStatus;
    }

    public void setEnquiryStatus(EnquiryStatus enquiryStatus) {
        this.enquiryStatus = enquiryStatus;
    }
}
