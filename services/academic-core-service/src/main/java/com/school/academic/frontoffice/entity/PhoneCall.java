package com.school.academic.frontoffice.entity;

import com.school.academic.frontoffice.enums.CallType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity for logging incoming and outgoing phone calls.
 */
@Entity
@Table(name = "front_office_phone_calls", schema = "academic_core")
public class PhoneCall extends FrontOfficeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "caller_name", nullable = false, length = 100)
    private String callerName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "call_date", nullable = false)
    private LocalDate callDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "call_type", nullable = false, length = 20)
    private CallType callType;

    @Column(name = "call_duration")
    private Integer callDuration; // Duration in minutes

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "next_follow_up_date")
    private LocalDate nextFollowUpDate;

    // Constructors
    public PhoneCall() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
}
