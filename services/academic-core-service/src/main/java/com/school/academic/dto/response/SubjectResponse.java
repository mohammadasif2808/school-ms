package com.school.academic.dto.response;

import java.util.UUID;

public class SubjectResponse {

    private UUID id;
    private String name;
    private String subjectCode;
    private Boolean isOptional;
    private String type;

    public SubjectResponse() {
    }

    public SubjectResponse(UUID id, String name, String subjectCode, Boolean isOptional, String type) {
        this.id = id;
        this.name = name;
        this.subjectCode = subjectCode;
        this.isOptional = isOptional;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public Boolean getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(Boolean isOptional) {
        this.isOptional = isOptional;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

