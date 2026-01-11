package com.school.academic.service.impl;

import com.school.academic.dto.request.CreateStaffRequest;
import com.school.academic.dto.response.StaffAssignmentResponse;
import com.school.academic.dto.response.StaffResponse;
import com.school.academic.service.StaffService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StaffServiceImpl implements StaffService {

    @Override
    public List<StaffResponse> listStaff() {
        // TODO: Implement staff listing logic
        throw new UnsupportedOperationException("Staff listing not yet implemented");
    }

    @Override
    public StaffResponse createStaff(CreateStaffRequest request) {
        // TODO: Implement staff creation logic
        throw new UnsupportedOperationException("Staff creation not yet implemented");
    }

    @Override
    public List<StaffAssignmentResponse> getStaffAssignments(UUID staffId, UUID academicYearId) {
        // TODO: Implement get staff assignments logic
        throw new UnsupportedOperationException("Get staff assignments not yet implemented");
    }
}

