package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassSectionResponse {

    private UUID id;
    private String className;
    private String sectionName;
    private String academicYear;
    private String medium;
    private UUID classTeacherId;
    private UUID classroomId;
}

