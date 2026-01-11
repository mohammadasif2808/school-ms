package com.school.academic.dto.request;

import java.util.UUID;

public class UpdateClassSectionRequest {

    private UUID classTeacherId;
    private UUID classroomId;
    private String medium;

    public UpdateClassSectionRequest() {
    }

    public UpdateClassSectionRequest(UUID classTeacherId, UUID classroomId, String medium) {
        this.classTeacherId = classTeacherId;
        this.classroomId = classroomId;
        this.medium = medium;
    }

    public UUID getClassTeacherId() {
        return classTeacherId;
    }

    public void setClassTeacherId(UUID classTeacherId) {
        this.classTeacherId = classTeacherId;
    }

    public UUID getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(UUID classroomId) {
        this.classroomId = classroomId;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }
}

