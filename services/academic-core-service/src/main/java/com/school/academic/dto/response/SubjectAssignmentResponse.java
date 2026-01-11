package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectAssignmentResponse {

    private UUID id;
    private String subjectName;
    private String className;
    private String sectionName;
}

