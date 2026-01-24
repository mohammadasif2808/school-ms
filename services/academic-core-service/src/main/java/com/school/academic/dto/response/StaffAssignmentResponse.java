package com.school.academic.dto.response;

import java.util.UUID;

public class StaffAssignmentResponse {

    private UUID id;
    private String staffName;
    private String subjectName;
    private String classSectionName;

    public StaffAssignmentResponse() {
    }

    public StaffAssignmentResponse(UUID id, String staffName, String subjectName, String classSectionName) {
        this.id = id;
        this.staffName = staffName;
        this.subjectName = subjectName;
        this.classSectionName = classSectionName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getClassSectionName() {
        return classSectionName;
    }

    public void setClassSectionName(String classSectionName) {
        this.classSectionName = classSectionName;
    }
}

