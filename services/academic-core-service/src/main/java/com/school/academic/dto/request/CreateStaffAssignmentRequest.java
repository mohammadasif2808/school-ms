package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateStaffAssignmentRequest {

    @NotNull(message = "Staff ID is required")
    private UUID staffId;

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    @NotNull(message = "Subject ID is required")
    private UUID subjectId;

    @NotNull(message = "Class section ID is required")
    private UUID classSectionId;

    public CreateStaffAssignmentRequest() {
    }

    public CreateStaffAssignmentRequest(UUID staffId, UUID academicYearId, UUID subjectId, UUID classSectionId) {
        this.staffId = staffId;
        this.academicYearId = academicYearId;
        this.subjectId = subjectId;
        this.classSectionId = classSectionId;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public UUID getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(UUID academicYearId) {
        this.academicYearId = academicYearId;
    }

    public UUID getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(UUID subjectId) {
        this.subjectId = subjectId;
    }

    public UUID getClassSectionId() {
        return classSectionId;
    }

    public void setClassSectionId(UUID classSectionId) {
        this.classSectionId = classSectionId;
    }
}

