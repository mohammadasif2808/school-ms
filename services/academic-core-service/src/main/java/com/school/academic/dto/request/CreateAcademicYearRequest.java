package com.school.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateAcademicYearRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Boolean isCurrent = false;

    public CreateAcademicYearRequest() {
    }

    public CreateAcademicYearRequest(String name, LocalDate startDate, LocalDate endDate, Boolean isCurrent) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCurrent = isCurrent;
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

