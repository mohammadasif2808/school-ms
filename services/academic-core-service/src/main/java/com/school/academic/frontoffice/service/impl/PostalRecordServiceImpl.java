package com.school.academic.frontoffice.service.impl;

import com.school.academic.frontoffice.dto.PageMetadata;
import com.school.academic.frontoffice.dto.postal.*;
import com.school.academic.frontoffice.entity.PostalRecord;
import com.school.academic.frontoffice.enums.PostalDirection;
import com.school.academic.frontoffice.enums.PostalType;
import com.school.academic.frontoffice.mapper.PostalRecordMapper;
import com.school.academic.frontoffice.repository.PostalRecordRepository;
import com.school.academic.frontoffice.service.PostalRecordService;
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
 * Service implementation for PostalRecord operations.
 */
@Service
@Transactional
public class PostalRecordServiceImpl implements PostalRecordService {

    private final PostalRecordRepository postalRecordRepository;
    private final PostalRecordMapper postalRecordMapper;

    public PostalRecordServiceImpl(PostalRecordRepository postalRecordRepository, PostalRecordMapper postalRecordMapper) {
        this.postalRecordRepository = postalRecordRepository;
        this.postalRecordMapper = postalRecordMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PostalRecordPageResponse listPostalRecords(
            UUID academicYearId,
            PostalDirection direction,
            PostalType postalType,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            Pageable pageable) {

        // Validate date range
        validateDateRange(fromDate, toDate);

        Page<PostalRecord> page = postalRecordRepository.findAllWithFilters(
                academicYearId, direction, postalType, fromDate, toDate, search, pageable);

        return new PostalRecordPageResponse(
                page.getContent().stream().map(postalRecordMapper::toResponse).collect(Collectors.toList()),
                PageMetadata.of(page)
        );
    }

    @Override
    public PostalRecordResponse createPostalRecord(UUID academicYearId, CreatePostalRecordRequest request) {
        // Validate postal date is not in future (for received items)
        if (request.getDirection() == PostalDirection.RECEIVED &&
            request.getDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("INVALID_RECEIVE_DATE",
                    "Receive date cannot be in the future");
        }

        // Validate attachment URL format if provided
        if (request.getAttachmentUrl() != null && !request.getAttachmentUrl().isBlank()) {
            if (!isValidUrl(request.getAttachmentUrl())) {
                throw new BusinessRuleException("INVALID_ATTACHMENT_URL",
                        "Attachment URL is not a valid URL format");
            }
        }

        PostalRecord record = postalRecordMapper.toEntity(request, academicYearId);
        // TODO: Set createdBy from security context
        PostalRecord saved = postalRecordRepository.save(record);
        return postalRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PostalRecordResponse getPostalRecordById(UUID id) {
        PostalRecord record = postalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postal record not found with id: " + id));
        return postalRecordMapper.toResponse(record);
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessRuleException("INVALID_DATE_RANGE",
                    "From date cannot be after to date");
        }
    }

    private boolean isValidUrl(String url) {
        try {
            new java.net.URL(url);
            return true;
        } catch (java.net.MalformedURLException e) {
            return false;
        }
    }
}
