package com.school.academic.frontoffice.repository;

import com.school.academic.frontoffice.entity.PhoneCall;
import com.school.academic.frontoffice.enums.CallType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Repository for PhoneCall entity.
 */
@Repository
public interface PhoneCallRepository extends JpaRepository<PhoneCall, UUID> {

    @Query("SELECT p FROM PhoneCall p WHERE p.academicYearId = :academicYearId " +
            "AND (:callType IS NULL OR p.callType = :callType) " +
            "AND (:fromDate IS NULL OR p.callDate >= :fromDate) " +
            "AND (:toDate IS NULL OR p.callDate <= :toDate) " +
            "AND (:hasFollowUp IS NULL OR (:hasFollowUp = true AND p.nextFollowUpDate IS NOT NULL) OR (:hasFollowUp = false AND p.nextFollowUpDate IS NULL)) " +
            "AND (:search IS NULL OR p.callerName LIKE %:search% OR p.phoneNumber LIKE %:search%)")
    Page<PhoneCall> findAllWithFilters(
            @Param("academicYearId") UUID academicYearId,
            @Param("callType") CallType callType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("hasFollowUp") Boolean hasFollowUp,
            @Param("search") String search,
            Pageable pageable);
}
