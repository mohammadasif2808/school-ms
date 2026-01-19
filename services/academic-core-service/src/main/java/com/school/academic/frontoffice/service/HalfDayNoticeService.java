package com.school.academic.frontoffice.service;

import com.school.academic.frontoffice.dto.halfday.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service interface for HalfDayNotice operations.
 */
public interface HalfDayNoticeService {

    HalfDayNoticePageResponse listHalfDayNotices(
            UUID schoolId,
            UUID academicYearId,
            UUID classId,
            UUID sectionId,
            LocalDate fromDate,
            LocalDate toDate,
            UUID studentId,
            Pageable pageable);

    HalfDayNoticeResponse createHalfDayNotice(UUID schoolId, UUID academicYearId, CreateHalfDayNoticeRequest request);

    HalfDayNoticeResponse getHalfDayNoticeById(UUID schoolId, UUID id);
}
