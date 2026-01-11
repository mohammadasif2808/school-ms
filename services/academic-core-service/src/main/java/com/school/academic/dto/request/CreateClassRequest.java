package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateClassRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Level order is required")
    private Integer levelOrder;

    private String description;

    public CreateClassRequest() {
    }

    public CreateClassRequest(String name, Integer levelOrder, String description) {
        this.name = name;
        this.levelOrder = levelOrder;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevelOrder() {
        return levelOrder;
    }

    public void setLevelOrder(Integer levelOrder) {
        this.levelOrder = levelOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

