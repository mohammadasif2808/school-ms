package com.school.academic.service;

import com.school.academic.dto.request.BulkPromoteRequest;
import com.school.academic.dto.request.CreateEnrollmentRequest;
import com.school.academic.dto.request.UpdateRollNumberRequest;
import com.school.academic.dto.response.EnrollmentResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EnrollmentService {

    List<EnrollmentResponse> getEnrollments(UUID academicYearId, UUID classId, UUID sectionId);

    EnrollmentResponse createEnrollment(CreateEnrollmentRequest request);

    Map<String, String> updateRollNumbers(List<UpdateRollNumberRequest> requests);

    Map<String, Object> bulkPromoteStudents(BulkPromoteRequest request);
}

