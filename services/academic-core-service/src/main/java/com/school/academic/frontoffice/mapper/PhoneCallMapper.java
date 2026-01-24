package com.school.academic.frontoffice.mapper;

import com.school.academic.frontoffice.dto.phonecall.CreatePhoneCallRequest;
import com.school.academic.frontoffice.dto.phonecall.PhoneCallResponse;
import com.school.academic.frontoffice.entity.PhoneCall;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper for PhoneCall entity and DTOs.
 */
@Component
public class PhoneCallMapper {

    public PhoneCall toEntity(CreatePhoneCallRequest request, UUID academicYearId) {
        PhoneCall phoneCall = new PhoneCall();
        phoneCall.setAcademicYearId(academicYearId);
        phoneCall.setCallerName(request.getCallerName());
        phoneCall.setPhoneNumber(request.getPhoneNumber());
        phoneCall.setCallDate(request.getCallDate());
        phoneCall.setCallType(request.getCallType());
        phoneCall.setCallDuration(request.getCallDuration());
        phoneCall.setDescription(request.getDescription());
        phoneCall.setNextFollowUpDate(request.getNextFollowUpDate());
        phoneCall.setRemarks(request.getRemarks());
        return phoneCall;
    }

    public PhoneCallResponse toResponse(PhoneCall entity) {
        PhoneCallResponse response = new PhoneCallResponse();
        response.setId(entity.getId());
        response.setCallerName(entity.getCallerName());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setCallDate(entity.getCallDate());
        response.setCallType(entity.getCallType());
        response.setCallDuration(entity.getCallDuration());
        response.setDescription(entity.getDescription());
        response.setNextFollowUpDate(entity.getNextFollowUpDate());
        response.setStatus(entity.getStatus());
        response.setRemarks(entity.getRemarks());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        return response;
    }
}
