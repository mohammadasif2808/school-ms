package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UpdateRollNumberRequest {

    private UUID academicYearId;

    @NotNull(message = "Enrollment ID is required")
    private UUID enrollmentId;

    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    public UpdateRollNumberRequest() {
    }

    public UpdateRollNumberRequest(UUID academicYearId, UUID enrollmentId, String rollNumber) {
        this.academicYearId = academicYearId;
        this.enrollmentId = enrollmentId;
        this.rollNumber = rollNumber;
    }

    public UUID getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(UUID academicYearId) {
        this.academicYearId = academicYearId;
    }

    public UUID getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(UUID enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }
}

