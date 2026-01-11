package com.school.academic.service.impl;

import com.school.academic.dto.request.CreateStaffAssignmentRequest;
import com.school.academic.dto.request.CreateSubjectAssignmentRequest;
import com.school.academic.dto.request.CreateSubjectRequest;
import com.school.academic.dto.response.StaffAssignmentResponse;
import com.school.academic.dto.response.SubjectAssignmentResponse;
import com.school.academic.dto.response.SubjectResponse;
import com.school.academic.service.CurriculumService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CurriculumServiceImpl implements CurriculumService {

    @Override
    public List<SubjectResponse> listSubjects() {
        // TODO: Implement subject listing logic
        throw new UnsupportedOperationException("Subject listing not yet implemented");
    }

    @Override
    public SubjectResponse createSubject(CreateSubjectRequest request) {
        // TODO: Implement subject creation logic
        throw new UnsupportedOperationException("Subject creation not yet implemented");
    }

    @Override
    public List<SubjectAssignmentResponse> listSubjectAssignments(UUID academicYearId, UUID classId) {
        // TODO: Implement subject assignment listing logic
        throw new UnsupportedOperationException("Subject assignment listing not yet implemented");
    }

    @Override
    public SubjectAssignmentResponse createSubjectAssignment(CreateSubjectAssignmentRequest request) {
        // TODO: Implement subject assignment creation logic
        throw new UnsupportedOperationException("Subject assignment creation not yet implemented");
    }

    @Override
    public StaffAssignmentResponse createStaffAssignment(CreateStaffAssignmentRequest request) {
        // TODO: Implement staff assignment creation logic
        throw new UnsupportedOperationException("Staff assignment creation not yet implemented");
    }
}

