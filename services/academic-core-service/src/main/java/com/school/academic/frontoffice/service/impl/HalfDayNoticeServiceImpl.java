package com.school.academic.frontoffice.service.impl;

import com.school.academic.frontoffice.dto.PageMetadata;
import com.school.academic.frontoffice.dto.halfday.*;
import com.school.academic.frontoffice.entity.HalfDayNotice;
import com.school.academic.frontoffice.mapper.HalfDayNoticeMapper;
import com.school.academic.frontoffice.repository.HalfDayNoticeRepository;
import com.school.academic.frontoffice.service.HalfDayNoticeService;
import com.school.academic.exception.BusinessRuleException;
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
 * Service implementation for HalfDayNotice operations.
 */
@Service
@Transactional
public class HalfDayNoticeServiceImpl implements HalfDayNoticeService {

    private final HalfDayNoticeRepository halfDayNoticeRepository;
    private final HalfDayNoticeMapper halfDayNoticeMapper;

    public HalfDayNoticeServiceImpl(HalfDayNoticeRepository halfDayNoticeRepository, HalfDayNoticeMapper halfDayNoticeMapper) {
        this.halfDayNoticeRepository = halfDayNoticeRepository;
        this.halfDayNoticeMapper = halfDayNoticeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public HalfDayNoticePageResponse listHalfDayNotices(
            UUID schoolId,
            UUID academicYearId,
            UUID classId,
            UUID sectionId,
            LocalDate fromDate,
            LocalDate toDate,
            UUID studentId,
            Pageable pageable) {

        // Validate date range
        validateDateRange(fromDate, toDate);

        // Note: Converting UUIDs to Long for repository query
        // In actual implementation, proper ID conversion should be handled
        Long classIdLong = classId != null ? classId.getMostSignificantBits() : null;
        Long sectionIdLong = sectionId != null ? sectionId.getMostSignificantBits() : null;
        Long studentIdLong = studentId != null ? studentId.getMostSignificantBits() : null;

        Page<HalfDayNotice> page = halfDayNoticeRepository.findAllWithFilters(
                schoolId, academicYearId, classIdLong, sectionIdLong, fromDate, toDate, studentIdLong, pageable);

        return new HalfDayNoticePageResponse(
                page.getContent().stream().map(halfDayNoticeMapper::toResponse).collect(Collectors.toList()),
                PageMetadata.of(page)
        );
    }

    @Override
    public HalfDayNoticeResponse createHalfDayNotice(UUID schoolId, UUID academicYearId, CreateHalfDayNoticeRequest request) {
        // Validate out time is not in future
        if (request.getOutTime().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("INVALID_OUT_TIME",
                    "Out time cannot be in the future");
        }

        // Validate out time is during reasonable school hours (6 AM to 8 PM)
        int hour = request.getOutTime().getHour();
        if (hour < 6 || hour > 20) {
            throw new BusinessRuleException("OUT_TIME_OUTSIDE_SCHOOL_HOURS",
                    "Out time must be during school hours (6:00 AM - 8:00 PM)");
        }

        HalfDayNotice notice = halfDayNoticeMapper.toEntity(request, schoolId, academicYearId);

        // TODO: Lookup student, class, section by UUID and set Long IDs
        // TODO: Validate student exists and belongs to the specified class/section
        // TODO: Denormalize student name, class name, section name
        // TODO: Set createdBy from security context

        HalfDayNotice saved = halfDayNoticeRepository.save(notice);
        return halfDayNoticeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public HalfDayNoticeResponse getHalfDayNoticeById(UUID schoolId, UUID id) {
        HalfDayNotice notice = halfDayNoticeRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Half day notice not found with id: " + id));
        return halfDayNoticeMapper.toResponse(notice);
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessRuleException("INVALID_DATE_RANGE",
                    "From date cannot be after to date");
        }
    }
}
