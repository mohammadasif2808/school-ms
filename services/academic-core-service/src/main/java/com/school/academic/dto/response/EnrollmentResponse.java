package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID classSectionId;
    private String rollNumber;
    private String status;
    private LocalDate enrollmentDate;
}

