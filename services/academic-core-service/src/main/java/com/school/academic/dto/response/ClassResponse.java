package com.school.academic.dto.response;

import java.util.UUID;

public class ClassResponse {

    private UUID id;
    private String name;
    private Integer levelOrder;
    private String description;

    public ClassResponse() {
    }

    public ClassResponse(UUID id, String name, Integer levelOrder, String description) {
        this.id = id;
        this.name = name;
        this.levelOrder = levelOrder;
        this.description = description;
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

