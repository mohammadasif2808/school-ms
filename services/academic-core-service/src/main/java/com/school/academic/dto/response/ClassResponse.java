package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {

    private UUID id;
    private String name;
    private Integer levelOrder;
    private String description;
}

