package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomResponse {

    private UUID id;
    private String roomNumber;
    private Integer capacity;
    private String infraType;
    private String buildingBlock;
}

