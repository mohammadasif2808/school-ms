package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkPromoteRequest {

    @NotNull(message = "Source academic year ID is required")
    private UUID sourceAcademicYearId;

    @NotNull(message = "Target academic year ID is required")
    private UUID targetAcademicYearId;

    private UUID sourceClassSectionId;
    private List<UUID> studentIds;
    private UUID targetClassSectionId;
    private String promotionStatus = "Promoted";
}

