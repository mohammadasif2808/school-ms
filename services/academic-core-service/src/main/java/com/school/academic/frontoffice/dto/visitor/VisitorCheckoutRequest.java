package com.school.academic.frontoffice.dto.visitor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request DTO for visitor checkout.
 */
public class VisitorCheckoutRequest {

    @NotNull(message = "Checkout time is required")
    private LocalDateTime checkOutTime;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public VisitorCheckoutRequest() {
    }

    // Getters and Setters
    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
