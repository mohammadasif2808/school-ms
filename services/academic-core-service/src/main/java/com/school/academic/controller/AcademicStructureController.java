package com.school.academic.controller;

import com.school.academic.dto.request.CreateAcademicYearRequest;
import com.school.academic.dto.request.CreateClassRequest;
import com.school.academic.dto.request.CreateClassSectionRequest;
import com.school.academic.dto.request.CreateSectionRequest;
import com.school.academic.dto.request.UpdateClassSectionRequest;
import com.school.academic.dto.response.AcademicYearResponse;
import com.school.academic.dto.response.ClassResponse;
import com.school.academic.dto.response.ClassSectionResponse;
import com.school.academic.dto.response.SectionResponse;
import com.school.academic.service.AcademicStructureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Academic Structure", description = "School hierarchy (Years, Classes, Sections)")
public class AcademicStructureController {

    private final AcademicStructureService academicStructureService;

    public AcademicStructureController(AcademicStructureService academicStructureService) {
        this.academicStructureService = academicStructureService;
    }

    // Academic Years
    @GetMapping("/academic-years")
    public ResponseEntity<List<AcademicYearResponse>> listAcademicYears() {
        List<AcademicYearResponse> years = academicStructureService.listAcademicYears();
        return ResponseEntity.ok(years);
    }

    @PostMapping("/academic-years")
    public ResponseEntity<AcademicYearResponse> createAcademicYear(@Valid @RequestBody CreateAcademicYearRequest request) {
        AcademicYearResponse response = academicStructureService.createAcademicYear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Classes
    @GetMapping("/classes")
    public ResponseEntity<List<ClassResponse>> listClasses() {
        List<ClassResponse> classes = academicStructureService.listClasses();
        return ResponseEntity.ok(classes);
    }

    @PostMapping("/classes")
    public ResponseEntity<ClassResponse> createClass(@Valid @RequestBody CreateClassRequest request) {
        ClassResponse response = academicStructureService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Sections
    @GetMapping("/sections")
    public ResponseEntity<List<SectionResponse>> listSections() {
        List<SectionResponse> sections = academicStructureService.listSections();
        return ResponseEntity.ok(sections);
    }

    @PostMapping("/sections")
    public ResponseEntity<SectionResponse> createSection(@Valid @RequestBody CreateSectionRequest request) {
        SectionResponse response = academicStructureService.createSection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Class Sections
    @GetMapping("/class-sections")
    public ResponseEntity<List<ClassSectionResponse>> listClassSections(
            @RequestParam UUID academicYearId,
            @RequestParam(required = false) UUID classId) {
        List<ClassSectionResponse> classSections = academicStructureService.listClassSections(academicYearId, classId);
        return ResponseEntity.ok(classSections);
    }

    @PostMapping("/class-sections")
    public ResponseEntity<ClassSectionResponse> createClassSection(@Valid @RequestBody CreateClassSectionRequest request) {
        ClassSectionResponse response = academicStructureService.createClassSection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/class-sections/{id}")
    public ResponseEntity<ClassSectionResponse> updateClassSection(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClassSectionRequest request) {
        ClassSectionResponse response = academicStructureService.updateClassSection(id, request);
        return ResponseEntity.ok(response);
    }
}

