package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassroomRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Capacity is required")
    private Integer capacity;

    @NotNull(message = "Infrastructure type is required")
    private String infraType;

    private String buildingBlock;
}

