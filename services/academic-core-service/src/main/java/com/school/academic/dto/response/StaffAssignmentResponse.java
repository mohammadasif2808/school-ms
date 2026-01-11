package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffAssignmentResponse {

    private UUID id;
    private String staffName;
    private String subjectName;
    private String classSectionName;
}

