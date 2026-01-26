package com.school.academic.dto.response;

import java.util.UUID;

public class ClassroomResponse {

    private UUID id;
    private String roomNumber;
    private String name;
    private Integer capacity;
    private String infraType;
    private String buildingBlock;
    private String status;

    public ClassroomResponse() {
    }

    public ClassroomResponse(UUID id, String roomNumber, String name, Integer capacity, String infraType, String buildingBlock, String status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.name = name;
        this.capacity = capacity;
        this.infraType = infraType;
        this.buildingBlock = buildingBlock;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getInfraType() {
        return infraType;
    }

    public void setInfraType(String infraType) {
        this.infraType = infraType;
    }

    public String getBuildingBlock() {
        return buildingBlock;
    }

    public void setBuildingBlock(String buildingBlock) {
        this.buildingBlock = buildingBlock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

