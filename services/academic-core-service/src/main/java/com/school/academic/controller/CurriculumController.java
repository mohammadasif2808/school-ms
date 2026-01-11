package com.school.academic.controller;

import com.school.academic.dto.request.CreateStaffAssignmentRequest;
import com.school.academic.dto.request.CreateSubjectAssignmentRequest;
import com.school.academic.dto.request.CreateSubjectRequest;
import com.school.academic.dto.response.StaffAssignmentResponse;
import com.school.academic.dto.response.SubjectAssignmentResponse;
import com.school.academic.dto.response.SubjectResponse;
import com.school.academic.service.CurriculumService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Curriculum", description = "Subject definitions and assignments")
public class CurriculumController {

    private final CurriculumService curriculumService;

    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    // Subjects
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectResponse>> listSubjects() {
        List<SubjectResponse> subjects = curriculumService.listSubjects();
        return ResponseEntity.ok(subjects);
    }

    @PostMapping("/subjects")
    public ResponseEntity<SubjectResponse> createSubject(@Valid @RequestBody CreateSubjectRequest request) {
        SubjectResponse response = curriculumService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Subject Assignments
    @GetMapping("/curriculum/subject-assignments")
    public ResponseEntity<List<SubjectAssignmentResponse>> listSubjectAssignments(
            @RequestParam UUID academicYearId,
            @RequestParam UUID classId) {
        List<SubjectAssignmentResponse> assignments = curriculumService.listSubjectAssignments(academicYearId, classId);
        return ResponseEntity.ok(assignments);
    }

    @PostMapping("/curriculum/subject-assignments")
    public ResponseEntity<SubjectAssignmentResponse> createSubjectAssignment(
            @Valid @RequestBody CreateSubjectAssignmentRequest request) {
        SubjectAssignmentResponse response = curriculumService.createSubjectAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Staff Assignments
    @PostMapping("/curriculum/staff-assignments")
    public ResponseEntity<StaffAssignmentResponse> createStaffAssignment(
            @Valid @RequestBody CreateStaffAssignmentRequest request) {
        StaffAssignmentResponse response = curriculumService.createStaffAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

