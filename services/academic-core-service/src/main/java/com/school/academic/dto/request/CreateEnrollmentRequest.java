package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateEnrollmentRequest {

    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Class section ID is required")
    private UUID classSectionId;

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    private String rollNumber;
    private String status = "Active";

    public CreateEnrollmentRequest() {
    }

    public CreateEnrollmentRequest(UUID studentId, UUID classSectionId, UUID academicYearId,
                                  String rollNumber, String status) {
        this.studentId = studentId;
        this.classSectionId = classSectionId;
        this.academicYearId = academicYearId;
        this.rollNumber = rollNumber;
        this.status = status;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public UUID getClassSectionId() {
        return classSectionId;
    }

    public void setClassSectionId(UUID classSectionId) {
        this.classSectionId = classSectionId;
    }

    public UUID getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(UUID academicYearId) {
        this.academicYearId = academicYearId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

