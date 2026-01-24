package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateClassSectionRequest {

    @NotNull(message = "Class ID is required")
    private UUID classId;

    @NotNull(message = "Section ID is required")
    private UUID sectionId;

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    private String medium = "English";

    public CreateClassSectionRequest() {
    }

    public CreateClassSectionRequest(UUID classId, UUID sectionId, UUID academicYearId, String medium) {
        this.classId = classId;
        this.sectionId = sectionId;
        this.academicYearId = academicYearId;
        this.medium = medium;
    }

    public UUID getClassId() {
        return classId;
    }

    public void setClassId(UUID classId) {
        this.classId = classId;
    }

    public UUID getSectionId() {
        return sectionId;
    }

    public void setSectionId(UUID sectionId) {
        this.sectionId = sectionId;
    }

    public UUID getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(UUID academicYearId) {
        this.academicYearId = academicYearId;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }
}

