package com.school.academic.dto.response;

import java.util.UUID;

public class ClassroomResponse {

    private UUID id;
    private String roomNumber;
    private Integer capacity;
    private String infraType;
    private String buildingBlock;

    public ClassroomResponse() {
    }

    public ClassroomResponse(UUID id, String roomNumber, Integer capacity, String infraType, String buildingBlock) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.infraType = infraType;
        this.buildingBlock = buildingBlock;
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
}

