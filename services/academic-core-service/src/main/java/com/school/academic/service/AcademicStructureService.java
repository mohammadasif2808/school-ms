package com.school.academic.service;

import com.school.academic.dto.request.CreateAcademicYearRequest;
import com.school.academic.dto.request.CreateClassRequest;
import com.school.academic.dto.request.CreateClassSectionRequest;
import com.school.academic.dto.request.CreateSectionRequest;
import com.school.academic.dto.request.UpdateClassSectionRequest;
import com.school.academic.dto.response.AcademicYearResponse;
import com.school.academic.dto.response.ClassResponse;
import com.school.academic.dto.response.ClassSectionResponse;
import com.school.academic.dto.response.SectionResponse;

import java.util.List;
import java.util.UUID;

public interface AcademicStructureService {

    // Academic Years
    List<AcademicYearResponse> listAcademicYears();

    AcademicYearResponse createAcademicYear(CreateAcademicYearRequest request);

    // Classes
    List<ClassResponse> listClasses();

    ClassResponse createClass(CreateClassRequest request);

    // Sections
    List<SectionResponse> listSections();

    SectionResponse createSection(CreateSectionRequest request);

    // Class Sections
    List<ClassSectionResponse> listClassSections(UUID academicYearId, UUID classId);

    ClassSectionResponse createClassSection(CreateClassSectionRequest request);

    ClassSectionResponse updateClassSection(UUID id, UpdateClassSectionRequest request);
}

