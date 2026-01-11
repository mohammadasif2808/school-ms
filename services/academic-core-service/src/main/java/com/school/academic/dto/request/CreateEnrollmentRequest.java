package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnrollmentRequest {

    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Class section ID is required")
    private UUID classSectionId;

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    private String rollNumber;
    private String status = "Active";
}

