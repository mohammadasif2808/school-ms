package com.school.academic.controller;

import com.school.academic.dto.request.CreateClassroomRequest;
import com.school.academic.dto.response.ClassroomResponse;
import com.school.academic.service.ClassroomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classrooms")
@Tag(name = "Classrooms", description = "Physical room management")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping
    public ResponseEntity<List<ClassroomResponse>> listClassrooms() {
        List<ClassroomResponse> classrooms = classroomService.listClassrooms();
        return ResponseEntity.ok(classrooms);
    }

    @PostMapping
    public ResponseEntity<ClassroomResponse> createClassroom(@Valid @RequestBody CreateClassroomRequest request) {
        ClassroomResponse response = classroomService.createClassroom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

