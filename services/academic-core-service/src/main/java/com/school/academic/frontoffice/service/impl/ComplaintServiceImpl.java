package com.school.academic.frontoffice.service.impl;

import com.school.academic.frontoffice.dto.PageMetadata;
import com.school.academic.frontoffice.dto.complaint.*;
import com.school.academic.frontoffice.entity.Complaint;
import com.school.academic.frontoffice.enums.ComplaintStatus;
import com.school.academic.frontoffice.enums.ComplaintType;
import com.school.academic.frontoffice.mapper.ComplaintMapper;
import com.school.academic.frontoffice.repository.ComplaintRepository;
import com.school.academic.frontoffice.service.ComplaintService;
import com.school.academic.exception.BusinessRuleException;
import com.school.academic.exception.InvalidStateTransitionException;
import com.school.academic.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for Complaint operations.
 */
@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintMapper complaintMapper;

    // Valid status transitions
    private static final java.util.Map<ComplaintStatus, Set<ComplaintStatus>> VALID_TRANSITIONS = java.util.Map.of(
            ComplaintStatus.OPEN, EnumSet.of(ComplaintStatus.IN_PROGRESS, ComplaintStatus.RESOLVED, ComplaintStatus.CLOSED),
            ComplaintStatus.IN_PROGRESS, EnumSet.of(ComplaintStatus.RESOLVED, ComplaintStatus.CLOSED),
            ComplaintStatus.RESOLVED, EnumSet.of(ComplaintStatus.CLOSED, ComplaintStatus.IN_PROGRESS), // Can reopen
            ComplaintStatus.CLOSED, EnumSet.noneOf(ComplaintStatus.class) // No transitions from CLOSED
    );

    public ComplaintServiceImpl(ComplaintRepository complaintRepository, ComplaintMapper complaintMapper) {
        this.complaintRepository = complaintRepository;
        this.complaintMapper = complaintMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintPageResponse listComplaints(
            UUID schoolId,
            UUID academicYearId,
            ComplaintStatus status,
            ComplaintType complaintType,
            String category,
            UUID assignedToStaffId,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            Pageable pageable) {

        // Validate date range
        validateDateRange(fromDate, toDate);

        // Note: Converting UUID to Long for repository query
        Long staffIdLong = assignedToStaffId != null ? assignedToStaffId.getMostSignificantBits() : null;

        Page<Complaint> page = complaintRepository.findAllWithFilters(
                schoolId, academicYearId, status, complaintType, category, staffIdLong, fromDate, toDate, search, pageable);

        return new ComplaintPageResponse(
                page.getContent().stream().map(complaintMapper::toResponse).collect(Collectors.toList()),
                PageMetadata.of(page)
        );
    }

    @Override
    public ComplaintResponse createComplaint(UUID schoolId, UUID academicYearId, CreateComplaintRequest request) {
        // Validate complaint date is not in future
        if (request.getComplaintDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("INVALID_COMPLAINT_DATE",
                    "Complaint date cannot be in the future");
        }

        Complaint complaint = complaintMapper.toEntity(request, schoolId, academicYearId);
        // TODO: Set createdBy from security context
        Complaint saved = complaintRepository.save(complaint);
        return complaintMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(UUID schoolId, UUID id) {
        Complaint complaint = complaintRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
        return complaintMapper.toResponse(complaint);
    }

    @Override
    public ComplaintResponse assignComplaint(UUID schoolId, UUID id, AssignComplaintRequest request) {
        Complaint complaint = complaintRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        // Cannot assign closed complaints
        if (complaint.getComplaintStatus() == ComplaintStatus.CLOSED) {
            throw new BusinessRuleException("CANNOT_ASSIGN_CLOSED",
                    "Cannot assign a closed complaint. Please reopen first.");
        }

        // Cannot assign resolved complaints without reopening
        if (complaint.getComplaintStatus() == ComplaintStatus.RESOLVED) {
            throw new BusinessRuleException("CANNOT_ASSIGN_RESOLVED",
                    "Cannot assign a resolved complaint. Please change status to IN_PROGRESS first.");
        }

        // TODO: Lookup staff by UUID and validate staff exists
        // TODO: Set assignedToStaffName (denormalized)
        complaint.setAssignedToStaffId(request.getAssignedToStaffId().getMostSignificantBits());

        if (request.getInternalNote() != null) {
            complaint.setInternalNote(request.getInternalNote());
        }

        // Auto-set status to IN_PROGRESS when assigned (if currently OPEN)
        if (complaint.getComplaintStatus() == ComplaintStatus.OPEN) {
            complaint.setComplaintStatus(ComplaintStatus.IN_PROGRESS);
        }
        // TODO: Set updatedBy from security context

        Complaint saved = complaintRepository.save(complaint);
        return complaintMapper.toResponse(saved);
    }

    @Override
    public ComplaintResponse updateComplaintStatus(UUID schoolId, UUID id, UpdateComplaintStatusRequest request) {
        Complaint complaint = complaintRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        ComplaintStatus currentStatus = complaint.getComplaintStatus();
        ComplaintStatus newStatus = request.getStatus();

        // Validate status transition
        validateStatusTransition(currentStatus, newStatus);

        // Validate action taken is provided when resolving
        if (newStatus == ComplaintStatus.RESOLVED &&
            (request.getActionTaken() == null || request.getActionTaken().isBlank())) {
            throw new BusinessRuleException("ACTION_REQUIRED_FOR_RESOLUTION",
                    "Action taken is required when resolving a complaint");
        }

        complaint.setComplaintStatus(newStatus);

        if (request.getActionTaken() != null) {
            complaint.setActionTaken(request.getActionTaken());
        }
        if (request.getInternalNote() != null) {
            complaint.setInternalNote(request.getInternalNote());
        }
        if (request.getRemarks() != null) {
            complaint.setRemarks(request.getRemarks());
        }
        // TODO: Set updatedBy from security context

        Complaint saved = complaintRepository.save(complaint);
        return complaintMapper.toResponse(saved);
    }

    private void validateStatusTransition(ComplaintStatus currentStatus, ComplaintStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // Same status is allowed (idempotent)
        }

        Set<ComplaintStatus> allowedTransitions = VALID_TRANSITIONS.get(currentStatus);
        if (allowedTransitions == null || !allowedTransitions.contains(newStatus)) {
            throw new InvalidStateTransitionException(
                    "INVALID_STATUS_TRANSITION",
                    currentStatus.name(),
                    newStatus.name(),
                    "Cannot transition complaint status from " + currentStatus + " to " + newStatus
            );
        }
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessRuleException("INVALID_DATE_RANGE",
                    "From date cannot be after to date");
        }
    }
}
