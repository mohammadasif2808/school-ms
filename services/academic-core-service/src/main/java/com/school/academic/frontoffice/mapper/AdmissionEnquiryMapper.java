package com.school.academic.frontoffice.mapper;

import com.school.academic.frontoffice.dto.enquiry.AdmissionEnquiryResponse;
import com.school.academic.frontoffice.dto.enquiry.CreateAdmissionEnquiryRequest;
import com.school.academic.frontoffice.entity.AdmissionEnquiry;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper for AdmissionEnquiry entity and DTOs.
 */
@Component
public class AdmissionEnquiryMapper {

    public AdmissionEnquiry toEntity(CreateAdmissionEnquiryRequest request, UUID schoolId, UUID academicYearId) {
        AdmissionEnquiry enquiry = new AdmissionEnquiry();
        enquiry.setSchoolId(schoolId);
        enquiry.setAcademicYearId(academicYearId);
        enquiry.setEnquirerName(request.getEnquirerName());
        enquiry.setPhoneNumber(request.getPhoneNumber());
        enquiry.setEnquiryType(request.getEnquiryType());
        enquiry.setSource(request.getSource());
        enquiry.setEnquiryDate(request.getEnquiryDate());
        enquiry.setDescription(request.getDescription());
        enquiry.setNextFollowUpDate(request.getNextFollowUpDate());
        enquiry.setRemarks(request.getRemarks());
        return enquiry;
    }

    public AdmissionEnquiryResponse toResponse(AdmissionEnquiry entity) {
        AdmissionEnquiryResponse response = new AdmissionEnquiryResponse();
        response.setId(entity.getId());
        response.setEnquirerName(entity.getEnquirerName());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setEnquiryType(entity.getEnquiryType());
        response.setSource(entity.getSource());
        response.setEnquiryDate(entity.getEnquiryDate());
        response.setDescription(entity.getDescription());
        response.setLastFollowUpDate(entity.getLastFollowUpDate());
        response.setNextFollowUpDate(entity.getNextFollowUpDate());
        response.setStatus(entity.getEnquiryStatus());
        response.setRemarks(entity.getRemarks());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }
}
