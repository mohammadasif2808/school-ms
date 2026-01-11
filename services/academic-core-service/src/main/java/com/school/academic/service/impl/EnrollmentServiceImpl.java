package com.school.academic.service.impl;

import com.school.academic.dto.request.BulkPromoteRequest;
import com.school.academic.dto.request.CreateEnrollmentRequest;
import com.school.academic.dto.request.UpdateRollNumberRequest;
import com.school.academic.dto.response.EnrollmentResponse;
import com.school.academic.service.EnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Override
    public List<EnrollmentResponse> getEnrollments(UUID academicYearId, UUID classId, UUID sectionId) {
        // TODO: Implement enrollment listing logic
        throw new UnsupportedOperationException("Enrollment listing not yet implemented");
    }

    @Override
    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {
        // TODO: Implement enrollment creation logic
        throw new UnsupportedOperationException("Enrollment creation not yet implemented");
    }

    @Override
    public Map<String, String> updateRollNumbers(List<UpdateRollNumberRequest> requests) {
        // TODO: Implement roll number update logic
        throw new UnsupportedOperationException("Roll number update not yet implemented");
    }

    @Override
    public Map<String, Object> bulkPromoteStudents(BulkPromoteRequest request) {
        // TODO: Implement bulk promotion logic
        throw new UnsupportedOperationException("Bulk promotion not yet implemented");
    }
}

