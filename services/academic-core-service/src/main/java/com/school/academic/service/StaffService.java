package com.school.academic.service;

import com.school.academic.dto.request.CreateStaffRequest;
import com.school.academic.dto.response.StaffAssignmentResponse;
import com.school.academic.dto.response.StaffResponse;

import java.util.List;
import java.util.UUID;

public interface StaffService {

    List<StaffResponse> listStaff();

    StaffResponse createStaff(CreateStaffRequest request);

    List<StaffAssignmentResponse> getStaffAssignments(UUID staffId, UUID academicYearId);
}

