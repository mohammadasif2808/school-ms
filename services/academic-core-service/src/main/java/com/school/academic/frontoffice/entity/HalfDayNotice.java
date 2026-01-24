package com.school.academic.frontoffice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for recording early departure of students during school hours.
 */
@Entity
@Table(name = "front_office_half_day_notices", schema = "academic_core")
public class HalfDayNotice extends FrontOfficeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    @Column(name = "out_time", nullable = false)
    private LocalDateTime outTime;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Column(name = "guardian_name", nullable = false, length = 100)
    private String guardianName;

    @Column(name = "guardian_phone", nullable = false, length = 20)
    private String guardianPhone;

    // Denormalized fields for display
    @Column(name = "student_name", length = 200)
    private String studentName;

    @Column(name = "class_name", length = 100)
    private String className;

    @Column(name = "section_name", length = 100)
    private String sectionName;

    // Constructors
    public HalfDayNotice() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
