package com.school.academic.frontoffice.controller;

import com.school.academic.frontoffice.dto.visitor.*;
import com.school.academic.frontoffice.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Controller for Visitor management.
 * Matches OpenAPI: /front-office/visitors
 */
@RestController
@RequestMapping("/api/v1/front-office/visitors")
@Tag(name = "Visitors", description = "Manage visitor entries and exits")
public class VisitorController {

    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @GetMapping
    @Operation(summary = "List visitors", description = "Retrieve a paginated list of visitors with optional filters.")
    public ResponseEntity<VisitorPageResponse> listVisitors(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Parameter(description = "Filter by visit purpose")
            @RequestParam(required = false) String purpose,
            @Parameter(description = "Filter visitors from this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter visitors until this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Search by visitor name or phone number")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filter by checkout status")
            @RequestParam(required = false) Boolean checkedOut,
            @PageableDefault(size = 20) Pageable pageable) {

        VisitorPageResponse response = visitorService.listVisitors(
                schoolId, academicYearId, purpose, fromDate, toDate, search, checkedOut, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Add visitor", description = "Record a new visitor entry.")
    public ResponseEntity<VisitorResponse> createVisitor(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Valid @RequestBody CreateVisitorRequest request) {

        VisitorResponse response = visitorService.createVisitor(schoolId, academicYearId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get visitor details", description = "Retrieve details of a specific visitor entry.")
    public ResponseEntity<VisitorResponse> getVisitorById(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @PathVariable UUID id) {

        VisitorResponse response = visitorService.getVisitorById(schoolId, id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/checkout")
    @Operation(summary = "Update visitor checkout time", description = "Record the checkout time for a visitor.")
    public ResponseEntity<VisitorResponse> checkoutVisitor(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @PathVariable UUID id,
            @Valid @RequestBody VisitorCheckoutRequest request) {

        VisitorResponse response = visitorService.checkoutVisitor(schoolId, id, request);
        return ResponseEntity.ok(response);
    }
}
