package com.school.academic.controller;

import com.school.academic.dto.request.CreateStaffRequest;
import com.school.academic.dto.response.StaffAssignmentResponse;
import com.school.academic.dto.response.StaffResponse;
import com.school.academic.service.StaffService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff", description = "Staff profile and assignment management")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<List<StaffResponse>> listStaff() {
        List<StaffResponse> staff = staffService.listStaff();
        return ResponseEntity.ok(staff);
    }

    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        StaffResponse response = staffService.createStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/assignments")
    public ResponseEntity<List<StaffAssignmentResponse>> getStaffAssignments(
            @PathVariable UUID id,
            @RequestParam UUID academicYearId) {
        List<StaffAssignmentResponse> assignments = staffService.getStaffAssignments(id, academicYearId);
        return ResponseEntity.ok(assignments);
    }
}

