package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private UUID id;
    private String admissionNumber;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private LocalDate joiningDate;
    private String status;
    private String userId;
}

