package com.school.academic.frontoffice.repository;

import com.school.academic.frontoffice.entity.Complaint;
import com.school.academic.frontoffice.enums.ComplaintStatus;
import com.school.academic.frontoffice.enums.ComplaintType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Complaint entity.
 */
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {

    @Query("SELECT c FROM Complaint c WHERE c.schoolId = :schoolId AND c.academicYearId = :academicYearId " +
            "AND (:status IS NULL OR c.complaintStatus = :status) " +
            "AND (:complaintType IS NULL OR c.complaintType = :complaintType) " +
            "AND (:category IS NULL OR c.category LIKE %:category%) " +
            "AND (:assignedToStaffId IS NULL OR c.assignedToStaffId = :assignedToStaffId) " +
            "AND (:fromDate IS NULL OR c.complaintDate >= :fromDate) " +
            "AND (:toDate IS NULL OR c.complaintDate <= :toDate) " +
            "AND (:search IS NULL OR c.complainantName LIKE %:search%)")
    Page<Complaint> findAllWithFilters(
            @Param("schoolId") UUID schoolId,
            @Param("academicYearId") UUID academicYearId,
            @Param("status") ComplaintStatus status,
            @Param("complaintType") ComplaintType complaintType,
            @Param("category") String category,
            @Param("assignedToStaffId") Long assignedToStaffId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            Pageable pageable);

    Optional<Complaint> findByIdAndSchoolId(UUID id, UUID schoolId);
}
