package com.school.academic.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class LinkGuardianRequest {

    @NotNull(message = "Parent ID is required")
    private UUID parentId;

    @NotNull(message = "Relationship is required")
    private String relationship;

    private Boolean isPrimaryContact = false;

    public LinkGuardianRequest() {
    }

    public LinkGuardianRequest(UUID parentId, String relationship, Boolean isPrimaryContact) {
        this.parentId = parentId;
        this.relationship = relationship;
        this.isPrimaryContact = isPrimaryContact;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Boolean getIsPrimaryContact() {
        return isPrimaryContact;
    }

    public void setIsPrimaryContact(Boolean isPrimaryContact) {
        this.isPrimaryContact = isPrimaryContact;
    }
}

