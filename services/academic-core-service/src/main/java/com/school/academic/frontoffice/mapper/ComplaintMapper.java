package com.school.academic.frontoffice.mapper;

import com.school.academic.frontoffice.dto.complaint.ComplaintResponse;
import com.school.academic.frontoffice.dto.complaint.CreateComplaintRequest;
import com.school.academic.frontoffice.entity.Complaint;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper for Complaint entity and DTOs.
 */
@Component
public class ComplaintMapper {

    public Complaint toEntity(CreateComplaintRequest request, UUID schoolId, UUID academicYearId) {
        Complaint complaint = new Complaint();
        complaint.setSchoolId(schoolId);
        complaint.setAcademicYearId(academicYearId);
        complaint.setComplainantName(request.getComplainantName());
        complaint.setComplaintType(request.getComplaintType());
        complaint.setCategory(request.getCategory());
        complaint.setComplaintDate(request.getComplaintDate());
        complaint.setDescription(request.getDescription());
        complaint.setInternalNote(request.getInternalNote());
        complaint.setRemarks(request.getRemarks());
        return complaint;
    }

    public ComplaintResponse toResponse(Complaint entity) {
        ComplaintResponse response = new ComplaintResponse();
        response.setId(entity.getId());
        response.setComplainantName(entity.getComplainantName());
        response.setComplaintType(entity.getComplaintType());
        response.setCategory(entity.getCategory());
        response.setComplaintDate(entity.getComplaintDate());
        response.setDescription(entity.getDescription());
        response.setActionTaken(entity.getActionTaken());
        // Note: assignedToStaffId is Long in entity, converting to UUID for API
        if (entity.getAssignedToStaffId() != null) {
            // Placeholder: In actual implementation, this should be proper UUID lookup
            response.setAssignedToStaffName(entity.getAssignedToStaffName());
        }
        response.setInternalNote(entity.getInternalNote());
        response.setStatus(entity.getComplaintStatus());
        response.setRemarks(entity.getRemarks());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }
}
