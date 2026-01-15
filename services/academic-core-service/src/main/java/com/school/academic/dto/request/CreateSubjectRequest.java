package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateSubjectRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Subject code is required")
    private String subjectCode;

    private Boolean isOptional = false;

    @NotNull(message = "Type is required")
    private String type;

    public CreateSubjectRequest() {
    }

    public CreateSubjectRequest(String name, String subjectCode, Boolean isOptional, String type) {
        this.name = name;
        this.subjectCode = subjectCode;
        this.isOptional = isOptional;
        this.type = type;
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

