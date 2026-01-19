package com.school.academic.frontoffice.service;

import com.school.academic.frontoffice.dto.phonecall.*;
import com.school.academic.frontoffice.enums.CallType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service interface for PhoneCall operations.
 */
public interface PhoneCallService {

    PhoneCallPageResponse listPhoneCalls(
            UUID schoolId,
            UUID academicYearId,
            CallType callType,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean hasFollowUp,
            String search,
            Pageable pageable);

    PhoneCallResponse createPhoneCall(UUID schoolId, UUID academicYearId, CreatePhoneCallRequest request);

    PhoneCallResponse getPhoneCallById(UUID schoolId, UUID id);
}
