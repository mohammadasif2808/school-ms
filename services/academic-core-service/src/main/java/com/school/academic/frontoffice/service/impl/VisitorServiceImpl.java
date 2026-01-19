package com.school.academic.frontoffice.service.impl;

import com.school.academic.frontoffice.dto.PageMetadata;
import com.school.academic.frontoffice.dto.visitor.*;
import com.school.academic.frontoffice.entity.Visitor;
import com.school.academic.frontoffice.enums.FrontOfficeStatus;
import com.school.academic.frontoffice.mapper.VisitorMapper;
import com.school.academic.frontoffice.repository.VisitorRepository;
import com.school.academic.frontoffice.service.VisitorService;
import com.school.academic.exception.BusinessRuleException;
import com.school.academic.exception.InvalidStateTransitionException;
import com.school.academic.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for Visitor operations.
 */
@Service
@Transactional
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    private final VisitorMapper visitorMapper;

    public VisitorServiceImpl(VisitorRepository visitorRepository, VisitorMapper visitorMapper) {
        this.visitorRepository = visitorRepository;
        this.visitorMapper = visitorMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public VisitorPageResponse listVisitors(
            UUID schoolId,
            UUID academicYearId,
            String purpose,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            Boolean checkedOut,
            Pageable pageable) {

        // Validate date range
        validateDateRange(fromDate, toDate);

        Page<Visitor> page = visitorRepository.findAllWithFilters(
                schoolId, academicYearId, purpose, fromDate, toDate, search, checkedOut, pageable);

        return new VisitorPageResponse(
                page.getContent().stream().map(visitorMapper::toResponse).collect(Collectors.toList()),
                PageMetadata.of(page)
        );
    }

    @Override
    public VisitorResponse createVisitor(UUID schoolId, UUID academicYearId, CreateVisitorRequest request) {
        // Validate check-in time is not in future
        if (request.getCheckInTime().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("INVALID_CHECK_IN_TIME",
                    "Check-in time cannot be in the future");
        }

        // Validate number of persons
        if (request.getNumberOfPersons() != null && request.getNumberOfPersons() > 100) {
            throw new BusinessRuleException("INVALID_PERSONS_COUNT",
                    "Number of persons cannot exceed 100");
        }

        Visitor visitor = visitorMapper.toEntity(request, schoolId, academicYearId);
        // TODO: Set createdBy from security context
        Visitor saved = visitorRepository.save(visitor);
        return visitorMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VisitorResponse getVisitorById(UUID schoolId, UUID id) {
        Visitor visitor = visitorRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor not found with id: " + id));
        return visitorMapper.toResponse(visitor);
    }

    @Override
    public VisitorResponse checkoutVisitor(UUID schoolId, UUID id, VisitorCheckoutRequest request) {
        Visitor visitor = visitorRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor not found with id: " + id));

        // Validate visitor is not already checked out
        if (visitor.getCheckOutTime() != null) {
            throw new InvalidStateTransitionException(
                    "ALREADY_CHECKED_OUT",
                    "CHECKED_IN",
                    "CHECKED_OUT",
                    "Visitor has already checked out at " + visitor.getCheckOutTime()
            );
        }

        // Validate visitor record is still active
        if (visitor.getStatus() != FrontOfficeStatus.ACTIVE) {
            throw new InvalidStateTransitionException(
                    "INVALID_RECORD_STATUS",
                    visitor.getStatus().name(),
                    "ACTIVE",
                    "Cannot checkout visitor. Record status is " + visitor.getStatus()
            );
        }

        // Validate checkout time is after check-in time
        if (request.getCheckOutTime().isBefore(visitor.getCheckInTime())) {
            throw new BusinessRuleException("INVALID_CHECKOUT_TIME",
                    "Checkout time must be after check-in time (" + visitor.getCheckInTime() + ")");
        }

        // Validate checkout time is not in future
        if (request.getCheckOutTime().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("FUTURE_CHECKOUT_TIME",
                    "Checkout time cannot be in the future");
        }

        visitor.setCheckOutTime(request.getCheckOutTime());
        if (request.getRemarks() != null) {
            visitor.setRemarks(request.getRemarks());
        }
        // TODO: Set updatedBy from security context

        Visitor saved = visitorRepository.save(visitor);
        return visitorMapper.toResponse(saved);
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessRuleException("INVALID_DATE_RANGE",
                    "From date cannot be after to date");
        }
    }
}
