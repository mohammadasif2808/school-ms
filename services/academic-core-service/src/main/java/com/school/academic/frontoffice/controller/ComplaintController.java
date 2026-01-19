package com.school.academic.frontoffice.controller;

import com.school.academic.frontoffice.dto.complaint.*;
import com.school.academic.frontoffice.enums.ComplaintStatus;
import com.school.academic.frontoffice.enums.ComplaintType;
import com.school.academic.frontoffice.service.ComplaintService;
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
 * Controller for Complaint management.
 * Matches OpenAPI: /front-office/complaints
 */
@RestController
@RequestMapping("/api/v1/front-office/complaints")
@Tag(name = "Complaints", description = "File and manage complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    @Operation(summary = "List complaints", description = "Retrieve a paginated list of complaints.")
    public ResponseEntity<ComplaintPageResponse> listComplaints(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Parameter(description = "Filter by complaint status")
            @RequestParam(required = false) ComplaintStatus status,
            @Parameter(description = "Filter by complaint type")
            @RequestParam(required = false) ComplaintType complaintType,
            @Parameter(description = "Filter by complaint category")
            @RequestParam(required = false) String category,
            @Parameter(description = "Filter by assigned staff member")
            @RequestParam(required = false) UUID assignedToStaffId,
            @Parameter(description = "Filter complaints from this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter complaints until this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Search by complainant name")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {

        ComplaintPageResponse response = complaintService.listComplaints(
                schoolId, academicYearId, status, complaintType, category, assignedToStaffId, fromDate, toDate, search, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "File complaint", description = "Record a new complaint.")
    public ResponseEntity<ComplaintResponse> createComplaint(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Valid @RequestBody CreateComplaintRequest request) {

        ComplaintResponse response = complaintService.createComplaint(schoolId, academicYearId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complaint details", description = "Retrieve details of a specific complaint.")
    public ResponseEntity<ComplaintResponse> getComplaintById(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @PathVariable UUID id) {

        ComplaintResponse response = complaintService.getComplaintById(schoolId, id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assign complaint to staff", description = "Assign a complaint to a staff member for resolution.")
    public ResponseEntity<ComplaintResponse> assignComplaint(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @PathVariable UUID id,
            @Valid @RequestBody AssignComplaintRequest request) {

        ComplaintResponse response = complaintService.assignComplaint(schoolId, id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update complaint status", description = "Update the status and action taken for a complaint.")
    public ResponseEntity<ComplaintResponse> updateComplaintStatus(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateComplaintStatusRequest request) {

        ComplaintResponse response = complaintService.updateComplaintStatus(schoolId, id, request);
        return ResponseEntity.ok(response);
    }
}
