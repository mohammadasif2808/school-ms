package com.school.academic.controller;

import com.school.academic.dto.request.CreateStudentRequest;
import com.school.academic.dto.request.LinkGuardianRequest;
import com.school.academic.dto.response.StudentResponse;
import com.school.academic.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Students", description = "Student profile management")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> listStudents(
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) UUID sectionId,
            @RequestParam(required = false) UUID academicYearId,
            @RequestParam(required = false) String name) {
        List<StudentResponse> students = studentService.listStudents(classId, sectionId, academicYearId, name);
        return ResponseEntity.ok(students);
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable UUID id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/guardians")
    public ResponseEntity<Map<String, String>> linkGuardian(
            @PathVariable UUID id,
            @Valid @RequestBody LinkGuardianRequest request) {
        studentService.linkGuardian(id, request);
        return ResponseEntity.ok(Map.of("message", "Guardian linked successfully"));
    }
}

