package com.school.academic.service;

import com.school.academic.dto.request.CreateStaffAssignmentRequest;
import com.school.academic.dto.request.CreateSubjectAssignmentRequest;
import com.school.academic.dto.request.CreateSubjectRequest;
import com.school.academic.dto.response.StaffAssignmentResponse;
import com.school.academic.dto.response.SubjectAssignmentResponse;
import com.school.academic.dto.response.SubjectResponse;

import java.util.List;
import java.util.UUID;

public interface CurriculumService {

    // Subjects
    List<SubjectResponse> listSubjects();

    SubjectResponse createSubject(CreateSubjectRequest request);

    // Subject Assignments
    List<SubjectAssignmentResponse> listSubjectAssignments(UUID academicYearId, UUID classId);

    SubjectAssignmentResponse createSubjectAssignment(CreateSubjectAssignmentRequest request);

    // Staff Assignments
    StaffAssignmentResponse createStaffAssignment(CreateStaffAssignmentRequest request);
}

