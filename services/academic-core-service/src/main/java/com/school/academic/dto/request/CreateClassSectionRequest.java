package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassSectionRequest {

    @NotNull(message = "Class ID is required")
    private UUID classId;

    @NotNull(message = "Section ID is required")
    private UUID sectionId;

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    private String medium = "English";
}

