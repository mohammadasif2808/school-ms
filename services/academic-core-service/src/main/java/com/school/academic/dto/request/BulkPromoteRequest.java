package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class BulkPromoteRequest {

    @NotNull(message = "Source academic year ID is required")
    private UUID sourceAcademicYearId;

    @NotNull(message = "Target academic year ID is required")
    private UUID targetAcademicYearId;

    private UUID sourceClassSectionId;
    private List<UUID> studentIds;
    private UUID targetClassSectionId;
    private String promotionStatus = "Promoted";

    public BulkPromoteRequest() {
    }

    public BulkPromoteRequest(UUID sourceAcademicYearId, UUID targetAcademicYearId, UUID sourceClassSectionId,
                             List<UUID> studentIds, UUID targetClassSectionId, String promotionStatus) {
        this.sourceAcademicYearId = sourceAcademicYearId;
        this.targetAcademicYearId = targetAcademicYearId;
        this.sourceClassSectionId = sourceClassSectionId;
        this.studentIds = studentIds;
        this.targetClassSectionId = targetClassSectionId;
        this.promotionStatus = promotionStatus;
    }

    public UUID getSourceAcademicYearId() {
        return sourceAcademicYearId;
    }

    public void setSourceAcademicYearId(UUID sourceAcademicYearId) {
        this.sourceAcademicYearId = sourceAcademicYearId;
    }

    public UUID getTargetAcademicYearId() {
        return targetAcademicYearId;
    }

    public void setTargetAcademicYearId(UUID targetAcademicYearId) {
        this.targetAcademicYearId = targetAcademicYearId;
    }

    public UUID getSourceClassSectionId() {
        return sourceClassSectionId;
    }

    public void setSourceClassSectionId(UUID sourceClassSectionId) {
        this.sourceClassSectionId = sourceClassSectionId;
    }

    public List<UUID> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<UUID> studentIds) {
        this.studentIds = studentIds;
    }

    public UUID getTargetClassSectionId() {
        return targetClassSectionId;
    }

    public void setTargetClassSectionId(UUID targetClassSectionId) {
        this.targetClassSectionId = targetClassSectionId;
    }

    public String getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(String promotionStatus) {
        this.promotionStatus = promotionStatus;
    }
}

