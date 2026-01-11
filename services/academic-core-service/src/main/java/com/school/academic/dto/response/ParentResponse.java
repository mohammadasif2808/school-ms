package com.school.academic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private String relationship;
}

