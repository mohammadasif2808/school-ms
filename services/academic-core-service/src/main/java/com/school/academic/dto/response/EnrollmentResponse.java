package com.school.academic.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public class EnrollmentResponse {

    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID classSectionId;
    private String rollNumber;
    private String status;
    private LocalDate enrollmentDate;

    public EnrollmentResponse() {
    }

    public EnrollmentResponse(UUID id, UUID studentId, String studentName, UUID classSectionId,
                             String rollNumber, String status, LocalDate enrollmentDate) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.classSectionId = classSectionId;
        this.rollNumber = rollNumber;
        this.status = status;
        this.enrollmentDate = enrollmentDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public UUID getClassSectionId() {
        return classSectionId;
    }

    public void setClassSectionId(UUID classSectionId) {
        this.classSectionId = classSectionId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}

