package com.school.academic.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public class AcademicYearResponse {

    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;

    public AcademicYearResponse() {
    }

    public AcademicYearResponse(UUID id, String name, LocalDate startDate, LocalDate endDate, Boolean isCurrent) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCurrent = isCurrent;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }
}

