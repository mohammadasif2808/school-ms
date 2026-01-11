package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponse {

    private UUID id;
    private String name;
    private String subjectCode;
    private Boolean isOptional;
    private String type;
}

