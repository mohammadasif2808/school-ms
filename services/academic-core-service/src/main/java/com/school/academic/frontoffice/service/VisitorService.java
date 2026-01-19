package com.school.academic.frontoffice.service;

import com.school.academic.frontoffice.dto.visitor.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service interface for Visitor operations.
 */
public interface VisitorService {

    VisitorPageResponse listVisitors(
            UUID schoolId,
            UUID academicYearId,
            String purpose,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            Boolean checkedOut,
            Pageable pageable);

    VisitorResponse createVisitor(UUID schoolId, UUID academicYearId, CreateVisitorRequest request);

    VisitorResponse getVisitorById(UUID schoolId, UUID id);

    VisitorResponse checkoutVisitor(UUID schoolId, UUID id, VisitorCheckoutRequest request);
}
