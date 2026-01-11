package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffAssignmentRequest {

    @NotNull(message = "Staff ID is required")
    private UUID staffId;

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    @NotNull(message = "Subject ID is required")
    private UUID subjectId;

    @NotNull(message = "Class section ID is required")
    private UUID classSectionId;
}

