package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubjectRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Subject code is required")
    private String subjectCode;

    private Boolean isOptional = false;

    @NotNull(message = "Type is required")
    private String type;
}

