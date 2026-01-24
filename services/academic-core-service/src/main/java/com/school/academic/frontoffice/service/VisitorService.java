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
            UUID academicYearId,
            String purpose,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            Boolean checkedOut,
            Pageable pageable);

    VisitorResponse createVisitor(UUID academicYearId, CreateVisitorRequest request);

    VisitorResponse getVisitorById(UUID id);

    VisitorResponse checkoutVisitor(UUID id, VisitorCheckoutRequest request);
}
