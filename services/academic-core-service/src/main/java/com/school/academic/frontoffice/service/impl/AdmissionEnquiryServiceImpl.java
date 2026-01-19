package com.school.academic.frontoffice.service.impl;

import com.school.academic.frontoffice.dto.PageMetadata;
import com.school.academic.frontoffice.dto.enquiry.*;
import com.school.academic.frontoffice.entity.AdmissionEnquiry;
import com.school.academic.frontoffice.enums.EnquirySource;
import com.school.academic.frontoffice.enums.EnquiryStatus;
import com.school.academic.frontoffice.enums.EnquiryType;
import com.school.academic.frontoffice.mapper.AdmissionEnquiryMapper;
import com.school.academic.frontoffice.repository.AdmissionEnquiryRepository;
import com.school.academic.frontoffice.service.AdmissionEnquiryService;
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
 * Service implementation for AdmissionEnquiry operations.
 */
@Service
@Transactional
public class AdmissionEnquiryServiceImpl implements AdmissionEnquiryService {

    private final AdmissionEnquiryRepository admissionEnquiryRepository;
    private final AdmissionEnquiryMapper admissionEnquiryMapper;

    // Valid status transitions
    private static final java.util.Map<EnquiryStatus, Set<EnquiryStatus>> VALID_TRANSITIONS = java.util.Map.of(
            EnquiryStatus.NEW, EnumSet.of(EnquiryStatus.FOLLOW_UP, EnquiryStatus.CONVERTED, EnquiryStatus.CLOSED),
            EnquiryStatus.FOLLOW_UP, EnumSet.of(EnquiryStatus.FOLLOW_UP, EnquiryStatus.CONVERTED, EnquiryStatus.CLOSED),
            EnquiryStatus.CONVERTED, EnumSet.of(EnquiryStatus.CLOSED),
            EnquiryStatus.CLOSED, EnumSet.noneOf(EnquiryStatus.class) // No transitions from CLOSED
    );

    public AdmissionEnquiryServiceImpl(AdmissionEnquiryRepository admissionEnquiryRepository,
                                       AdmissionEnquiryMapper admissionEnquiryMapper) {
        this.admissionEnquiryRepository = admissionEnquiryRepository;
        this.admissionEnquiryMapper = admissionEnquiryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AdmissionEnquiryPageResponse listAdmissionEnquiries(
            UUID schoolId,
            UUID academicYearId,
            EnquiryStatus status,
            EnquirySource source,
            EnquiryType enquiryType,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean hasFollowUp,
            String search,
            Pageable pageable) {

        // Validate date range
        validateDateRange(fromDate, toDate);

        Page<AdmissionEnquiry> page = admissionEnquiryRepository.findAllWithFilters(
                schoolId, academicYearId, status, source, enquiryType, fromDate, toDate, hasFollowUp, search, pageable);

        return new AdmissionEnquiryPageResponse(
                page.getContent().stream().map(admissionEnquiryMapper::toResponse).collect(Collectors.toList()),
                PageMetadata.of(page)
        );
    }

    @Override
    public AdmissionEnquiryResponse createAdmissionEnquiry(UUID schoolId, UUID academicYearId, CreateAdmissionEnquiryRequest request) {
        // Validate enquiry date is not in future
        if (request.getEnquiryDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("INVALID_ENQUIRY_DATE",
                    "Enquiry date cannot be in the future");
        }

        // Validate next follow-up date is after enquiry date
        if (request.getNextFollowUpDate() != null &&
            request.getNextFollowUpDate().isBefore(request.getEnquiryDate())) {
            throw new BusinessRuleException("INVALID_FOLLOW_UP_DATE",
                    "Next follow-up date must be on or after the enquiry date");
        }

        AdmissionEnquiry enquiry = admissionEnquiryMapper.toEntity(request, schoolId, academicYearId);
        // TODO: Set createdBy from security context
        AdmissionEnquiry saved = admissionEnquiryRepository.save(enquiry);
        return admissionEnquiryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AdmissionEnquiryResponse getAdmissionEnquiryById(UUID schoolId, UUID id) {
        AdmissionEnquiry enquiry = admissionEnquiryRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission enquiry not found with id: " + id));
        return admissionEnquiryMapper.toResponse(enquiry);
    }

    @Override
    public AdmissionEnquiryResponse updateEnquiryStatus(UUID schoolId, UUID id, UpdateEnquiryStatusRequest request) {
        AdmissionEnquiry enquiry = admissionEnquiryRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission enquiry not found with id: " + id));

        EnquiryStatus currentStatus = enquiry.getEnquiryStatus();
        EnquiryStatus newStatus = request.getStatus();

        // Validate status transition
        validateStatusTransition(currentStatus, newStatus);

        enquiry.setEnquiryStatus(newStatus);
        if (request.getRemarks() != null) {
            enquiry.setRemarks(request.getRemarks());
        }
        // TODO: Set updatedBy from security context

        AdmissionEnquiry saved = admissionEnquiryRepository.save(enquiry);
        return admissionEnquiryMapper.toResponse(saved);
    }

    @Override
    public AdmissionEnquiryResponse updateEnquiryFollowUp(UUID schoolId, UUID id, UpdateEnquiryFollowUpRequest request) {
        AdmissionEnquiry enquiry = admissionEnquiryRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission enquiry not found with id: " + id));

        // Cannot update follow-up for closed or converted enquiries
        if (enquiry.getEnquiryStatus() == EnquiryStatus.CLOSED ||
            enquiry.getEnquiryStatus() == EnquiryStatus.CONVERTED) {
            throw new BusinessRuleException("FOLLOW_UP_NOT_ALLOWED",
                    "Cannot update follow-up for " + enquiry.getEnquiryStatus() + " enquiries");
        }

        // Validate last follow-up date is not in future
        if (request.getLastFollowUpDate() != null &&
            request.getLastFollowUpDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("INVALID_LAST_FOLLOW_UP_DATE",
                    "Last follow-up date cannot be in the future");
        }

        // Validate next follow-up date logic
        if (request.getNextFollowUpDate() != null) {
            LocalDate lastFollowUp = request.getLastFollowUpDate() != null ?
                    request.getLastFollowUpDate() : enquiry.getLastFollowUpDate();

            if (lastFollowUp != null && request.getNextFollowUpDate().isBefore(lastFollowUp)) {
                throw new BusinessRuleException("INVALID_NEXT_FOLLOW_UP_DATE",
                        "Next follow-up date must be on or after the last follow-up date");
            }
        }

        if (request.getLastFollowUpDate() != null) {
            enquiry.setLastFollowUpDate(request.getLastFollowUpDate());
        }
        if (request.getNextFollowUpDate() != null) {
            enquiry.setNextFollowUpDate(request.getNextFollowUpDate());
        }
        if (request.getRemarks() != null) {
            enquiry.setRemarks(request.getRemarks());
        }
        // TODO: Set updatedBy from security context

        AdmissionEnquiry saved = admissionEnquiryRepository.save(enquiry);
        return admissionEnquiryMapper.toResponse(saved);
    }

    private void validateStatusTransition(EnquiryStatus currentStatus, EnquiryStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // Same status is allowed (idempotent)
        }

        Set<EnquiryStatus> allowedTransitions = VALID_TRANSITIONS.get(currentStatus);
        if (allowedTransitions == null || !allowedTransitions.contains(newStatus)) {
            throw new InvalidStateTransitionException(
                    "INVALID_STATUS_TRANSITION",
                    currentStatus.name(),
                    newStatus.name(),
                    "Cannot transition enquiry status from " + currentStatus + " to " + newStatus
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
