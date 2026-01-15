package com.school.academic.dto.response;

import java.util.UUID;

public class SubjectAssignmentResponse {

    private UUID id;
    private String subjectName;
    private String className;
    private String sectionName;

    public SubjectAssignmentResponse() {
    }

    public SubjectAssignmentResponse(UUID id, String subjectName, String className, String sectionName) {
        this.id = id;
        this.subjectName = subjectName;
        this.className = className;
        this.sectionName = sectionName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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
}

