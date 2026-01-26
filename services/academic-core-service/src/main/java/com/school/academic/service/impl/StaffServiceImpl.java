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
        staff.setGender(request.getGender());
        staff.setDateOfBirth(request.getDateOfBirth());
        staff.setDesignation(request.getDesignation());
        staff.setQualification(request.getQualification());
        staff.setProfessionalQualification(request.getProfessionalQualification());
        staff.setWorkExperience(request.getWorkExperience());
        staff.setMobile(request.getMobile());
        staff.setEmail(request.getEmail());
        staff.setJoiningDate(request.getJoiningDate());
        staff.setAadharNumber(request.getAadharNumber());
        staff.setBloodGroup(request.getBloodGroup());
        staff.setMaritalStatus(request.getMaritalStatus());
        staff.setFatherName(request.getFatherName());
        staff.setMotherName(request.getMotherName());
        staff.setStaffType(request.getStaffType());

        // Permanent Address
        staff.setPermanentAddress(request.getPermanentAddress());
        staff.setPermanentCity(request.getPermanentCity());
        staff.setPermanentState(request.getPermanentState());
        staff.setPermanentPostalCode(request.getPermanentPostalCode());

        // Current Address
        staff.setCurrentAddress(request.getCurrentAddress());
        staff.setCurrentCity(request.getCurrentCity());
        staff.setCurrentState(request.getCurrentState());
        staff.setCurrentPostalCode(request.getCurrentPostalCode());

        // Social Media
        staff.setFacebookUrl(request.getFacebookUrl());
        staff.setTwitterUrl(request.getTwitterUrl());
        staff.setLinkedinUrl(request.getLinkedinUrl());
        staff.setInstagramUrl(request.getInstagramUrl());

        // Notes
        staff.setNotes(request.getNotes());

        // File URLs
        staff.setPhotoUrl(request.getPhotoUrl());
        staff.setResumeUrl(request.getResumeUrl());

        staff.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");

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
        response.setGender(entity.getGender());
        response.setDateOfBirth(entity.getDateOfBirth());
        response.setDesignation(entity.getDesignation());
        response.setQualification(entity.getQualification());
        response.setProfessionalQualification(entity.getProfessionalQualification());
        response.setWorkExperience(entity.getWorkExperience());
        response.setMobile(entity.getMobile());
        response.setEmail(entity.getEmail());
        response.setJoiningDate(entity.getJoiningDate());
        response.setAadharNumber(entity.getAadharNumber());
        response.setBloodGroup(entity.getBloodGroup());
        response.setMaritalStatus(entity.getMaritalStatus());
        response.setFatherName(entity.getFatherName());
        response.setMotherName(entity.getMotherName());
        response.setStaffType(entity.getStaffType());

        // Permanent Address
        response.setPermanentAddress(entity.getPermanentAddress());
        response.setPermanentCity(entity.getPermanentCity());
        response.setPermanentState(entity.getPermanentState());
        response.setPermanentPostalCode(entity.getPermanentPostalCode());

        // Current Address
        response.setCurrentAddress(entity.getCurrentAddress());
        response.setCurrentCity(entity.getCurrentCity());
        response.setCurrentState(entity.getCurrentState());
        response.setCurrentPostalCode(entity.getCurrentPostalCode());

        // Social Media
        response.setFacebookUrl(entity.getFacebookUrl());
        response.setTwitterUrl(entity.getTwitterUrl());
        response.setLinkedinUrl(entity.getLinkedinUrl());
        response.setInstagramUrl(entity.getInstagramUrl());

        // Notes
        response.setNotes(entity.getNotes());

        // File URLs
        response.setPhotoUrl(entity.getPhotoUrl());
        response.setResumeUrl(entity.getResumeUrl());

        response.setStatus(entity.getStatus());
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

