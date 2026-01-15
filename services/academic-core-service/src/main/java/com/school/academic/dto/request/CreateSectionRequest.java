package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateSectionRequest {

    @NotBlank(message = "Name is required")
    private String name;

    public CreateSectionRequest() {
    }

    public CreateSectionRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

