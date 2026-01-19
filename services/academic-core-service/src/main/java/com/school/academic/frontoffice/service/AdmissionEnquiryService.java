package com.school.academic.frontoffice.service;

import com.school.academic.frontoffice.dto.enquiry.*;
import com.school.academic.frontoffice.enums.EnquirySource;
import com.school.academic.frontoffice.enums.EnquiryStatus;
import com.school.academic.frontoffice.enums.EnquiryType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service interface for AdmissionEnquiry operations.
 */
public interface AdmissionEnquiryService {

    AdmissionEnquiryPageResponse listAdmissionEnquiries(
            UUID schoolId,
            UUID academicYearId,
            EnquiryStatus status,
            EnquirySource source,
            EnquiryType enquiryType,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean hasFollowUp,
            String search,
            Pageable pageable);

    AdmissionEnquiryResponse createAdmissionEnquiry(UUID schoolId, UUID academicYearId, CreateAdmissionEnquiryRequest request);

    AdmissionEnquiryResponse getAdmissionEnquiryById(UUID schoolId, UUID id);

    AdmissionEnquiryResponse updateEnquiryStatus(UUID schoolId, UUID id, UpdateEnquiryStatusRequest request);

    AdmissionEnquiryResponse updateEnquiryFollowUp(UUID schoolId, UUID id, UpdateEnquiryFollowUpRequest request);
}
