package com.school.academic.frontoffice.mapper;

import com.school.academic.frontoffice.dto.visitor.CreateVisitorRequest;
import com.school.academic.frontoffice.dto.visitor.VisitorResponse;
import com.school.academic.frontoffice.entity.Visitor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper for Visitor entity and DTOs.
 */
@Component
public class VisitorMapper {

    public Visitor toEntity(CreateVisitorRequest request, UUID academicYearId) {
        Visitor visitor = new Visitor();
        visitor.setAcademicYearId(academicYearId);
        visitor.setVisitorName(request.getVisitorName());
        visitor.setPhoneNumber(request.getPhoneNumber());
        visitor.setPurpose(request.getPurpose());
        visitor.setNumberOfPersons(request.getNumberOfPersons());
        visitor.setIdProofType(request.getIdProofType());
        visitor.setIdProofNumber(request.getIdProofNumber());
        visitor.setCheckInTime(request.getCheckInTime());
        visitor.setRemarks(request.getRemarks());
        return visitor;
    }

    public VisitorResponse toResponse(Visitor entity) {
        VisitorResponse response = new VisitorResponse();
        response.setId(entity.getId());
        response.setVisitorName(entity.getVisitorName());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setPurpose(entity.getPurpose());
        response.setNumberOfPersons(entity.getNumberOfPersons());
        response.setIdProofType(entity.getIdProofType());
        response.setIdProofNumber(entity.getIdProofNumber());
        response.setCheckInTime(entity.getCheckInTime());
        response.setCheckOutTime(entity.getCheckOutTime());
        response.setRemarks(entity.getRemarks());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        return response;
    }
}
