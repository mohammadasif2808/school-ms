package com.school.academic.frontoffice.mapper;

import com.school.academic.frontoffice.dto.postal.CreatePostalRecordRequest;
import com.school.academic.frontoffice.dto.postal.PostalRecordResponse;
import com.school.academic.frontoffice.entity.PostalRecord;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper for PostalRecord entity and DTOs.
 */
@Component
public class PostalRecordMapper {

    public PostalRecord toEntity(CreatePostalRecordRequest request, UUID schoolId, UUID academicYearId) {
        PostalRecord record = new PostalRecord();
        record.setSchoolId(schoolId);
        record.setAcademicYearId(academicYearId);
        record.setDirection(request.getDirection());
        record.setPostalType(request.getPostalType());
        record.setReferenceNumber(request.getReferenceNumber());
        record.setFromTitle(request.getFromTitle());
        record.setToTitle(request.getToTitle());
        record.setCourierName(request.getCourierName());
        record.setDate(request.getDate());
        record.setAttachmentUrl(request.getAttachmentUrl());
        record.setNotes(request.getNotes());
        record.setRemarks(request.getRemarks());
        return record;
    }

    public PostalRecordResponse toResponse(PostalRecord entity) {
        PostalRecordResponse response = new PostalRecordResponse();
        response.setId(entity.getId());
        response.setDirection(entity.getDirection());
        response.setPostalType(entity.getPostalType());
        response.setReferenceNumber(entity.getReferenceNumber());
        response.setFromTitle(entity.getFromTitle());
        response.setToTitle(entity.getToTitle());
        response.setCourierName(entity.getCourierName());
        response.setDate(entity.getDate());
        response.setAttachmentUrl(entity.getAttachmentUrl());
        response.setNotes(entity.getNotes());
        response.setStatus(entity.getStatus());
        response.setRemarks(entity.getRemarks());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        return response;
    }
}
