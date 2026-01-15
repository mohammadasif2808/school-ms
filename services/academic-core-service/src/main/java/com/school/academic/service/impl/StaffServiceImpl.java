package com.school.academic.service.impl;

import com.school.academic.domain.Staff;
import com.school.academic.domain.StaffAssignment;
import com.school.academic.dto.request.CreateStaffRequest;
import com.school.academic.dto.response.StaffAssignmentResponse;
import com.school.academic.dto.response.StaffResponse;
import com.school.academic.exception.DuplicateResourceException;
import com.school.academic.exception.ResourceNotFoundException;
import com.school.academic.repository.StaffAssignmentRepository;
import com.school.academic.repository.StaffRepository;
import com.school.academic.service.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class StaffServiceImpl implements StaffService {

    private static final Logger log = LoggerFactory.getLogger(StaffServiceImpl.class);

    private final StaffRepository staffRepository;
    private final StaffAssignmentRepository staffAssignmentRepository;

    public StaffServiceImpl(StaffRepository staffRepository,
                           StaffAssignmentRepository staffAssignmentRepository) {
        this.staffRepository = staffRepository;
        this.staffAssignmentRepository = staffAssignmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> listStaff() {
        log.debug("Listing all staff");
        return staffRepository.findAll().stream()
                .map(this::toStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StaffResponse createStaff(CreateStaffRequest request) {
        log.info("Creating staff with employee ID: {}", request.getEmployeeId());

        if (staffRepository.existsByStaffCode(request.getEmployeeId())) {
            throw new DuplicateResourceException("DUPLICATE_EMPLOYEE_ID",
                    "Staff with employee ID '" + request.getEmployeeId() + "' already exists");
        }

        Staff staff = new Staff();
        staff.setStaffCode(request.getEmployeeId());
        staff.setUserId(request.getUserId());
        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setDesignation(request.getDesignation());
        staff.setQualification(request.getQualification());
        staff.setMobile(request.getMobile());
        staff.setEmail(request.getEmail());
        staff.setStatus("ACTIVE");

        Staff saved = staffRepository.save(staff);
        log.info("Created staff with id: {}", saved.getId());

        return toStaffResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffAssignmentResponse> getStaffAssignments(UUID staffId, UUID academicYearId) {
        log.debug("Getting assignments for staffId: {}, academicYearId: {}", staffId, academicYearId);

        Long staffIdLong = uuidToLong(staffId);
        Long yearIdLong = uuidToLong(academicYearId);

        // Verify staff exists
        if (!staffRepository.existsById(staffIdLong)) {
            throw new ResourceNotFoundException("Staff not found with id: " + staffId);
        }

        List<StaffAssignment> assignments = staffAssignmentRepository.findByStaffIdAndAcademicYearId(
                staffIdLong, yearIdLong);

        return assignments.stream()
                .map(this::toStaffAssignmentResponse)
                .collect(Collectors.toList());
    }

    // ==================== Mappers ====================

    private StaffResponse toStaffResponse(Staff entity) {
        StaffResponse response = new StaffResponse();
        response.setId(longToUuid(entity.getId()));
        response.setEmployeeId(entity.getStaffCode());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setDesignation(entity.getDesignation());
        response.setQualification(entity.getQualification());
        response.setMobile(entity.getMobile());
        response.setEmail(entity.getEmail());
        return response;
    }

    private StaffAssignmentResponse toStaffAssignmentResponse(StaffAssignment entity) {
        StaffAssignmentResponse response = new StaffAssignmentResponse();
        response.setId(longToUuid(entity.getId()));
        response.setStaffName(entity.getStaff().getFullName());
        response.setSubjectName(entity.getSubject().getName());
        response.setClassSectionName(entity.getClassSection().getDisplayName());
        return response;
    }

    // ==================== Utility Methods ====================

    private Long uuidToLong(UUID uuid) {
        if (uuid == null) return null;
        return uuid.getLeastSignificantBits() & Long.MAX_VALUE;
    }

    private UUID longToUuid(Long id) {
        if (id == null) return null;
        return new UUID(0L, id);
    }
}

