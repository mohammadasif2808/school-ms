package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateSubjectAssignmentRequest {

    @NotNull(message = "Subject ID is required")
    private UUID subjectId;

    @NotNull(message = "Class ID is required")
    private UUID classId;

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    private UUID sectionId;

    public CreateSubjectAssignmentRequest() {
    }

    public CreateSubjectAssignmentRequest(UUID subjectId, UUID classId, UUID academicYearId, UUID sectionId) {
        this.subjectId = subjectId;
        this.classId = classId;
        this.academicYearId = academicYearId;
        this.sectionId = sectionId;
    }

    public UUID getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(UUID subjectId) {
        this.subjectId = subjectId;
    }

    public UUID getClassId() {
        return classId;
    }

    public void setClassId(UUID classId) {
        this.classId = classId;
    }

    public UUID getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(UUID academicYearId) {
        this.academicYearId = academicYearId;
    }

    public UUID getSectionId() {
        return sectionId;
    }

    public void setSectionId(UUID sectionId) {
        this.sectionId = sectionId;
    }
}

