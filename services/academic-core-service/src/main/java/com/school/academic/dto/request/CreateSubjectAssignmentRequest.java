package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubjectAssignmentRequest {

    @NotNull(message = "Subject ID is required")
    private UUID subjectId;

    @NotNull(message = "Class ID is required")
    private UUID classId;

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    private UUID sectionId;
}

