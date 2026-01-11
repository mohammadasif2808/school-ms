package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {

    private UUID id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String designation;
    private String qualification;
    private String mobile;
    private String email;
}

