package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Level order is required")
    private Integer levelOrder;

    private String description;
}

