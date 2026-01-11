package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYearResponse {

    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
}

