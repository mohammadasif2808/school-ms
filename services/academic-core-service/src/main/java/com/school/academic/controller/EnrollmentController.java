package com.school.academic.controller;

import com.school.academic.dto.request.BulkPromoteRequest;
import com.school.academic.dto.request.CreateEnrollmentRequest;
import com.school.academic.dto.request.UpdateRollNumberRequest;
import com.school.academic.dto.response.EnrollmentResponse;
import com.school.academic.service.EnrollmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
@Tag(name = "Enrollment", description = "Student placement and promotion")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getEnrollments(
            @RequestParam UUID academicYearId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) UUID sectionId) {
        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollments(academicYearId, classId, sectionId);
        return ResponseEntity.ok(enrollments);
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(@Valid @RequestBody CreateEnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.createEnrollment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/roll-numbers")
    public ResponseEntity<Map<String, String>> updateRollNumbers(
            @Valid @RequestBody List<UpdateRollNumberRequest> requests) {
        Map<String, String> response = enrollmentService.updateRollNumbers(requests);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/promote")
    public ResponseEntity<Map<String, Object>> bulkPromoteStudents(@Valid @RequestBody BulkPromoteRequest request) {
        Map<String, Object> response = enrollmentService.bulkPromoteStudents(request);
        return ResponseEntity.ok(response);
    }
}

