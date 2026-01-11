package com.school.academic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassSectionRequest {

    private UUID classTeacherId;
    private UUID classroomId;
    private String medium;
}

