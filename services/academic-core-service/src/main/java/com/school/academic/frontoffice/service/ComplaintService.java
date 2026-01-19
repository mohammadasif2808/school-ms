package com.school.academic.frontoffice.service;

import com.school.academic.frontoffice.dto.complaint.*;
import com.school.academic.frontoffice.enums.ComplaintStatus;
import com.school.academic.frontoffice.enums.ComplaintType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service interface for Complaint operations.
 */
public interface ComplaintService {

    ComplaintPageResponse listComplaints(
            UUID schoolId,
            UUID academicYearId,
            ComplaintStatus status,
            ComplaintType complaintType,
            String category,
            UUID assignedToStaffId,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            Pageable pageable);

    ComplaintResponse createComplaint(UUID schoolId, UUID academicYearId, CreateComplaintRequest request);

    ComplaintResponse getComplaintById(UUID schoolId, UUID id);

    ComplaintResponse assignComplaint(UUID schoolId, UUID id, AssignComplaintRequest request);

    ComplaintResponse updateComplaintStatus(UUID schoolId, UUID id, UpdateComplaintStatusRequest request);
}
