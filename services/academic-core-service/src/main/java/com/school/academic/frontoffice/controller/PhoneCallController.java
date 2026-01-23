package com.school.academic.frontoffice.controller;

import com.school.academic.frontoffice.dto.phonecall.*;
import com.school.academic.frontoffice.enums.CallType;
import com.school.academic.frontoffice.service.PhoneCallService;
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
 * Controller for Phone Call management.
 * Matches OpenAPI: /front-office/phone-calls
 */
@RestController
@RequestMapping("/api/v1/front-office/phone-calls")
@Tag(name = "Phone Calls", description = "Log incoming and outgoing phone calls")
public class PhoneCallController {

    private final PhoneCallService phoneCallService;

    public PhoneCallController(PhoneCallService phoneCallService) {
        this.phoneCallService = phoneCallService;
    }

    @GetMapping
    @Operation(summary = "List phone calls", description = "Retrieve a paginated list of phone call logs.")
    public ResponseEntity<PhoneCallPageResponse> listPhoneCalls(
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Parameter(description = "Filter by call type (INCOMING/OUTGOING)")
            @RequestParam(required = false) CallType callType,
            @Parameter(description = "Filter calls from this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter calls until this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Filter calls with pending follow-up")
            @RequestParam(required = false) Boolean hasFollowUp,
            @Parameter(description = "Search by caller name or phone number")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {

        PhoneCallPageResponse response = phoneCallService.listPhoneCalls(
                academicYearId, callType, fromDate, toDate, hasFollowUp, search, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Log phone call", description = "Record a new phone call entry.")
    public ResponseEntity<PhoneCallResponse> createPhoneCall(
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Valid @RequestBody CreatePhoneCallRequest request) {

        PhoneCallResponse response = phoneCallService.createPhoneCall(academicYearId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get phone call details", description = "Retrieve details of a specific phone call log.")
    public ResponseEntity<PhoneCallResponse> getPhoneCallById(
            @PathVariable UUID id) {

        PhoneCallResponse response = phoneCallService.getPhoneCallById(id);
        return ResponseEntity.ok(response);
    }
}
