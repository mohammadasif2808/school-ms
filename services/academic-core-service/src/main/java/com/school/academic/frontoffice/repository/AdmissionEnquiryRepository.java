package com.school.academic.frontoffice.repository;

import com.school.academic.frontoffice.entity.AdmissionEnquiry;
import com.school.academic.frontoffice.enums.EnquirySource;
import com.school.academic.frontoffice.enums.EnquiryStatus;
import com.school.academic.frontoffice.enums.EnquiryType;
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
 * Repository for AdmissionEnquiry entity.
 */
@Repository
public interface AdmissionEnquiryRepository extends JpaRepository<AdmissionEnquiry, UUID> {

    @Query("SELECT e FROM AdmissionEnquiry e WHERE e.schoolId = :schoolId AND e.academicYearId = :academicYearId " +
            "AND (:status IS NULL OR e.enquiryStatus = :status) " +
            "AND (:source IS NULL OR e.source = :source) " +
            "AND (:enquiryType IS NULL OR e.enquiryType = :enquiryType) " +
            "AND (:fromDate IS NULL OR e.enquiryDate >= :fromDate) " +
            "AND (:toDate IS NULL OR e.enquiryDate <= :toDate) " +
            "AND (:hasFollowUp IS NULL OR (:hasFollowUp = true AND e.nextFollowUpDate IS NOT NULL) OR (:hasFollowUp = false AND e.nextFollowUpDate IS NULL)) " +
            "AND (:search IS NULL OR e.enquirerName LIKE %:search% OR e.phoneNumber LIKE %:search%)")
    Page<AdmissionEnquiry> findAllWithFilters(
            @Param("schoolId") UUID schoolId,
            @Param("academicYearId") UUID academicYearId,
            @Param("status") EnquiryStatus status,
            @Param("source") EnquirySource source,
            @Param("enquiryType") EnquiryType enquiryType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("hasFollowUp") Boolean hasFollowUp,
            @Param("search") String search,
            Pageable pageable);

    Optional<AdmissionEnquiry> findByIdAndSchoolId(UUID id, UUID schoolId);
}
