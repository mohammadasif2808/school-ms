package com.school.academic.frontoffice.entity;

import com.school.academic.frontoffice.enums.ComplaintStatus;
import com.school.academic.frontoffice.enums.ComplaintType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity for tracking and resolving complaints from parents, students, or staff.
 */
@Entity
@Table(name = "front_office_complaints", schema = "academic_core")
public class Complaint extends FrontOfficeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "complainant_name", nullable = false, length = 100)
    private String complainantName;

    @Enumerated(EnumType.STRING)
    @Column(name = "complaint_type", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private ComplaintType complaintType;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "complaint_date", nullable = false)
    private LocalDate complaintDate;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "action_taken", length = 2000)
    private String actionTaken;

    @Column(name = "assigned_to_staff_id")
    private Long assignedToStaffId;

    @Column(name = "assigned_to_staff_name", length = 200)
    private String assignedToStaffName; // Denormalized for display

    @Column(name = "internal_note", length = 1000)
    private String internalNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "complaint_status", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private ComplaintStatus complaintStatus = ComplaintStatus.OPEN;

    // Constructors
    public Complaint() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getComplainantName() {
        return complainantName;
    }

    public void setComplainantName(String complainantName) {
        this.complainantName = complainantName;
    }

    public ComplaintType getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(ComplaintType complaintType) {
        this.complaintType = complaintType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(LocalDate complaintDate) {
        this.complaintDate = complaintDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public Long getAssignedToStaffId() {
        return assignedToStaffId;
    }

    public void setAssignedToStaffId(Long assignedToStaffId) {
        this.assignedToStaffId = assignedToStaffId;
    }

    public String getAssignedToStaffName() {
        return assignedToStaffName;
    }

    public void setAssignedToStaffName(String assignedToStaffName) {
        this.assignedToStaffName = assignedToStaffName;
    }

    public String getInternalNote() {
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }

    public ComplaintStatus getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(ComplaintStatus complaintStatus) {
        this.complaintStatus = complaintStatus;
    }
}
