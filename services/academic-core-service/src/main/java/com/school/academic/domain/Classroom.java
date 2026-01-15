package com.school.academic.domain;

import jakarta.persistence.*;

/**
 * Classroom entity - Represents a physical brick-and-mortar room.
 */
@Entity
@Table(name = "classroom", schema = "academic_core")
public class Classroom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true, length = 32)
    private String roomNumber;

    @Column(name = "name", length = 128)
    private String name;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "infra_type", length = 32)
    private String infraType; // Lecture, Lab, Hall

    @Column(name = "building_block", length = 64)
    private String buildingBlock;

    @Column(name = "status", length = 32)
    private String status = "ACTIVE";

    // Default constructor
    public Classroom() {
    }

    // Builder-style constructor
    public Classroom(String roomNumber, String name, Integer capacity, String infraType, String buildingBlock) {
        this.roomNumber = roomNumber;
        this.name = name;
        this.capacity = capacity;
        this.infraType = infraType;
        this.buildingBlock = buildingBlock;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
