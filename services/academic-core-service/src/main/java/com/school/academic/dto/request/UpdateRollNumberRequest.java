package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRollNumberRequest {

    private UUID academicYearId;

    @NotNull(message = "Enrollment ID is required")
    private UUID enrollmentId;

    @NotBlank(message = "Roll number is required")
    private String rollNumber;
}

