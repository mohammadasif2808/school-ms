package com.school.academic.frontoffice.controller;

import com.school.academic.frontoffice.dto.enquiry.*;
import com.school.academic.frontoffice.enums.EnquirySource;
import com.school.academic.frontoffice.enums.EnquiryStatus;
import com.school.academic.frontoffice.enums.EnquiryType;
import com.school.academic.frontoffice.service.AdmissionEnquiryService;
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
 * Controller for Admission Enquiry management.
 * Matches OpenAPI: /front-office/admission-enquiries
 */
@RestController
@RequestMapping("/api/v1/front-office/admission-enquiries")
@Tag(name = "Admission Enquiries", description = "Track prospective student enquiries")
public class AdmissionEnquiryController {

    private final AdmissionEnquiryService admissionEnquiryService;

    public AdmissionEnquiryController(AdmissionEnquiryService admissionEnquiryService) {
        this.admissionEnquiryService = admissionEnquiryService;
    }

    @GetMapping
    @Operation(summary = "List admission enquiries", description = "Retrieve a paginated list of admission enquiries.")
    public ResponseEntity<AdmissionEnquiryPageResponse> listAdmissionEnquiries(
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Parameter(description = "Filter by enquiry status")
            @RequestParam(required = false) EnquiryStatus status,
            @Parameter(description = "Filter by enquiry source")
            @RequestParam(required = false) EnquirySource source,
            @Parameter(description = "Filter by enquirer type")
            @RequestParam(required = false) EnquiryType enquiryType,
            @Parameter(description = "Filter enquiries from this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter enquiries until this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Filter enquiries with pending follow-up")
            @RequestParam(required = false) Boolean hasFollowUp,
            @Parameter(description = "Search by enquirer name or phone number")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {

        AdmissionEnquiryPageResponse response = admissionEnquiryService.listAdmissionEnquiries(
                academicYearId, status, source, enquiryType, fromDate, toDate, hasFollowUp, search, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create admission enquiry", description = "Record a new admission enquiry.")
    public ResponseEntity<AdmissionEnquiryResponse> createAdmissionEnquiry(
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Valid @RequestBody CreateAdmissionEnquiryRequest request) {

        AdmissionEnquiryResponse response = admissionEnquiryService.createAdmissionEnquiry(academicYearId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get admission enquiry details", description = "Retrieve details of a specific admission enquiry.")
    public ResponseEntity<AdmissionEnquiryResponse> getAdmissionEnquiryById(
            @PathVariable UUID id) {

        AdmissionEnquiryResponse response = admissionEnquiryService.getAdmissionEnquiryById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update admission enquiry status", description = "Update the status of an admission enquiry.")
    public ResponseEntity<AdmissionEnquiryResponse> updateEnquiryStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEnquiryStatusRequest request) {

        AdmissionEnquiryResponse response = admissionEnquiryService.updateEnquiryStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/follow-up")
    @Operation(summary = "Update admission enquiry follow-up", description = "Update the follow-up dates of an admission enquiry.")
    public ResponseEntity<AdmissionEnquiryResponse> updateEnquiryFollowUp(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEnquiryFollowUpRequest request) {

        AdmissionEnquiryResponse response = admissionEnquiryService.updateEnquiryFollowUp(id, request);
        return ResponseEntity.ok(response);
    }
}
