package com.school.academic.frontoffice.controller;

import com.school.academic.frontoffice.dto.postal.*;
import com.school.academic.frontoffice.enums.PostalDirection;
import com.school.academic.frontoffice.enums.PostalType;
import com.school.academic.frontoffice.service.PostalRecordService;
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
 * Controller for Postal Record management.
 * Matches OpenAPI: /front-office/postal-records
 */
@RestController
@RequestMapping("/api/v1/front-office/postal-records")
@Tag(name = "Postal", description = "Manage postal dispatch and receive records")
public class PostalController {

    private final PostalRecordService postalRecordService;

    public PostalController(PostalRecordService postalRecordService) {
        this.postalRecordService = postalRecordService;
    }

    @GetMapping
    @Operation(summary = "List postal records", description = "Retrieve a paginated list of postal dispatch and receive records.")
    public ResponseEntity<PostalRecordPageResponse> listPostalRecords(
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Parameter(description = "Filter by direction (RECEIVED/DISPATCHED)")
            @RequestParam(required = false) PostalDirection direction,
            @Parameter(description = "Filter by postal type (Letter/Parcel/Courier)")
            @RequestParam(required = false) PostalType postalType,
            @Parameter(description = "Filter records from this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter records until this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Search by reference number, sender, or receiver")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {

        PostalRecordPageResponse response = postalRecordService.listPostalRecords(
                academicYearId, direction, postalType, fromDate, toDate, search, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Add postal record", description = "Record a new postal dispatch or receive entry.")
    public ResponseEntity<PostalRecordResponse> createPostalRecord(
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Valid @RequestBody CreatePostalRecordRequest request) {

        PostalRecordResponse response = postalRecordService.createPostalRecord(academicYearId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get postal record details", description = "Retrieve details of a specific postal record.")
    public ResponseEntity<PostalRecordResponse> getPostalRecordById(
            @PathVariable UUID id) {

        PostalRecordResponse response = postalRecordService.getPostalRecordById(id);
        return ResponseEntity.ok(response);
    }
}
