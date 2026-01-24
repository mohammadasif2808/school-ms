package com.school.academic.frontoffice.service;

import com.school.academic.frontoffice.dto.postal.*;
import com.school.academic.frontoffice.enums.PostalDirection;
import com.school.academic.frontoffice.enums.PostalType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service interface for PostalRecord operations.
 */
public interface PostalRecordService {

    PostalRecordPageResponse listPostalRecords(
            UUID academicYearId,
            PostalDirection direction,
            PostalType postalType,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            Pageable pageable);

    PostalRecordResponse createPostalRecord(UUID academicYearId, CreatePostalRecordRequest request);

    PostalRecordResponse getPostalRecordById(UUID id);
}
