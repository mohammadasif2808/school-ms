package com.school.academic.frontoffice.service.impl;

import com.school.academic.frontoffice.dto.PageMetadata;
import com.school.academic.frontoffice.dto.phonecall.*;
import com.school.academic.frontoffice.entity.PhoneCall;
import com.school.academic.frontoffice.enums.CallType;
import com.school.academic.frontoffice.mapper.PhoneCallMapper;
import com.school.academic.frontoffice.repository.PhoneCallRepository;
import com.school.academic.frontoffice.service.PhoneCallService;
import com.school.academic.exception.BusinessRuleException;
import com.school.academic.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for PhoneCall operations.
 */
@Service
@Transactional
public class PhoneCallServiceImpl implements PhoneCallService {

    private final PhoneCallRepository phoneCallRepository;
    private final PhoneCallMapper phoneCallMapper;

    public PhoneCallServiceImpl(PhoneCallRepository phoneCallRepository, PhoneCallMapper phoneCallMapper) {
        this.phoneCallRepository = phoneCallRepository;
        this.phoneCallMapper = phoneCallMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PhoneCallPageResponse listPhoneCalls(
            UUID schoolId,
            UUID academicYearId,
            CallType callType,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean hasFollowUp,
            String search,
            Pageable pageable) {

        // Validate date range
        validateDateRange(fromDate, toDate);

        Page<PhoneCall> page = phoneCallRepository.findAllWithFilters(
                schoolId, academicYearId, callType, fromDate, toDate, hasFollowUp, search, pageable);

        return new PhoneCallPageResponse(
                page.getContent().stream().map(phoneCallMapper::toResponse).collect(Collectors.toList()),
                PageMetadata.of(page)
        );
    }

    @Override
    public PhoneCallResponse createPhoneCall(UUID schoolId, UUID academicYearId, CreatePhoneCallRequest request) {
        // Validate call date is not in future
        if (request.getCallDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("INVALID_CALL_DATE",
                    "Call date cannot be in the future");
        }

        // Validate follow-up date is on or after call date
        if (request.getNextFollowUpDate() != null &&
            request.getNextFollowUpDate().isBefore(request.getCallDate())) {
            throw new BusinessRuleException("INVALID_FOLLOW_UP_DATE",
                    "Follow-up date must be on or after the call date");
        }

        // Validate call duration if provided
        if (request.getCallDuration() != null && request.getCallDuration() > 480) {
            throw new BusinessRuleException("INVALID_CALL_DURATION",
                    "Call duration cannot exceed 480 minutes (8 hours)");
        }

        PhoneCall phoneCall = phoneCallMapper.toEntity(request, schoolId, academicYearId);
        // TODO: Set createdBy from security context
        PhoneCall saved = phoneCallRepository.save(phoneCall);
        return phoneCallMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PhoneCallResponse getPhoneCallById(UUID schoolId, UUID id) {
        PhoneCall phoneCall = phoneCallRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Phone call not found with id: " + id));
        return phoneCallMapper.toResponse(phoneCall);
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessRuleException("INVALID_DATE_RANGE",
                    "From date cannot be after to date");
        }
    }
}
