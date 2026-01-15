package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateClassroomRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Capacity is required")
    private Integer capacity;

    @NotNull(message = "Infrastructure type is required")
    private String infraType;

    private String buildingBlock;

    public CreateClassroomRequest() {
    }

    public CreateClassroomRequest(String roomNumber, Integer capacity, String infraType, String buildingBlock) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.infraType = infraType;
        this.buildingBlock = buildingBlock;
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

