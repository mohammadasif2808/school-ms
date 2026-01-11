package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkGuardianRequest {

    @NotNull(message = "Parent ID is required")
    private UUID parentId;

    @NotNull(message = "Relationship is required")
    private String relationship;

    private Boolean isPrimaryContact = false;
}

