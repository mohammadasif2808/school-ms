package com.school.academic.dto.response;

import java.util.UUID;

public class ClassSectionResponse {

    private UUID id;
    private String className;
    private String sectionName;
    private String academicYear;
    private String medium;
    private UUID classTeacherId;
    private UUID classroomId;

    public ClassSectionResponse() {
    }

    public ClassSectionResponse(UUID id, String className, String sectionName, String academicYear,
                                String medium, UUID classTeacherId, UUID classroomId) {
        this.id = id;
        this.className = className;
        this.sectionName = sectionName;
        this.academicYear = academicYear;
        this.medium = medium;
        this.classTeacherId = classTeacherId;
        this.classroomId = classroomId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
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
}

