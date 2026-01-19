package com.school.academic.frontoffice.controller;

import com.school.academic.frontoffice.dto.halfday.*;
import com.school.academic.frontoffice.service.HalfDayNoticeService;
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
 * Controller for Half Day Notice management.
 * Matches OpenAPI: /front-office/half-day-notices
 */
@RestController
@RequestMapping("/api/v1/front-office/half-day-notices")
@Tag(name = "Half Day Notices", description = "Track student early departures")
public class HalfDayNoticeController {

    private final HalfDayNoticeService halfDayNoticeService;

    public HalfDayNoticeController(HalfDayNoticeService halfDayNoticeService) {
        this.halfDayNoticeService = halfDayNoticeService;
    }

    @GetMapping
    @Operation(summary = "List half day notices", description = "Retrieve a paginated list of half day notices.")
    public ResponseEntity<HalfDayNoticePageResponse> listHalfDayNotices(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Parameter(description = "Filter by class")
            @RequestParam(required = false) UUID classId,
            @Parameter(description = "Filter by section")
            @RequestParam(required = false) UUID sectionId,
            @Parameter(description = "Filter notices from this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter notices until this date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Filter by specific student")
            @RequestParam(required = false) UUID studentId,
            @PageableDefault(size = 20) Pageable pageable) {

        HalfDayNoticePageResponse response = halfDayNoticeService.listHalfDayNotices(
                schoolId, academicYearId, classId, sectionId, fromDate, toDate, studentId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create half day notice", description = "Record a student's early departure.")
    public ResponseEntity<HalfDayNoticeResponse> createHalfDayNotice(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @Parameter(description = "The academic year context for the request", required = true)
            @RequestHeader("X-Academic-Year-Id") UUID academicYearId,
            @Valid @RequestBody CreateHalfDayNoticeRequest request) {

        HalfDayNoticeResponse response = halfDayNoticeService.createHalfDayNotice(schoolId, academicYearId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get half day notice details", description = "Retrieve details of a specific half day notice.")
    public ResponseEntity<HalfDayNoticeResponse> getHalfDayNoticeById(
            @Parameter(description = "The school context for the request", required = true)
            @RequestHeader("X-School-Id") UUID schoolId,
            @PathVariable UUID id) {

        HalfDayNoticeResponse response = halfDayNoticeService.getHalfDayNoticeById(schoolId, id);
        return ResponseEntity.ok(response);
    }
}
