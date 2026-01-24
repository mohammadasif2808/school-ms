package com.school.academic.frontoffice.mapper;

import com.school.academic.frontoffice.dto.halfday.CreateHalfDayNoticeRequest;
import com.school.academic.frontoffice.dto.halfday.HalfDayNoticeResponse;
import com.school.academic.frontoffice.entity.HalfDayNotice;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper for HalfDayNotice entity and DTOs.
 */
@Component
public class HalfDayNoticeMapper {

    public HalfDayNotice toEntity(CreateHalfDayNoticeRequest request, UUID academicYearId) {
        HalfDayNotice notice = new HalfDayNotice();
        notice.setAcademicYearId(academicYearId);
        // Note: studentId, classId, sectionId are Long in entity but UUID in request
        // This will need adaptation in service layer to lookup actual IDs
        notice.setOutTime(request.getOutTime());
        notice.setReason(request.getReason());
        notice.setGuardianName(request.getGuardianName());
        notice.setGuardianPhone(request.getGuardianPhone());
        notice.setRemarks(request.getRemarks());
        return notice;
    }

    public HalfDayNoticeResponse toResponse(HalfDayNotice entity) {
        HalfDayNoticeResponse response = new HalfDayNoticeResponse();
        response.setId(entity.getId());
        // Note: Converting Long IDs to UUIDs for API consistency
        // In actual implementation, this may need proper UUID generation or lookup
        response.setStudentName(entity.getStudentName());
        response.setClassName(entity.getClassName());
        response.setSectionName(entity.getSectionName());
        response.setOutTime(entity.getOutTime());
        response.setReason(entity.getReason());
        response.setGuardianName(entity.getGuardianName());
        response.setGuardianPhone(entity.getGuardianPhone());
        response.setStatus(entity.getStatus());
        response.setRemarks(entity.getRemarks());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        return response;
    }
}
