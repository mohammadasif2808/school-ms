package com.school.academic.frontoffice.repository;

import com.school.academic.frontoffice.entity.PostalRecord;
import com.school.academic.frontoffice.enums.PostalDirection;
import com.school.academic.frontoffice.enums.PostalType;
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
 * Repository for PostalRecord entity.
 */
@Repository
public interface PostalRecordRepository extends JpaRepository<PostalRecord, UUID> {

    @Query("SELECT p FROM PostalRecord p WHERE p.schoolId = :schoolId AND p.academicYearId = :academicYearId " +
            "AND (:direction IS NULL OR p.direction = :direction) " +
            "AND (:postalType IS NULL OR p.postalType = :postalType) " +
            "AND (:fromDate IS NULL OR p.date >= :fromDate) " +
            "AND (:toDate IS NULL OR p.date <= :toDate) " +
            "AND (:search IS NULL OR p.referenceNumber LIKE %:search% OR p.fromTitle LIKE %:search% OR p.toTitle LIKE %:search%)")
    Page<PostalRecord> findAllWithFilters(
            @Param("schoolId") UUID schoolId,
            @Param("academicYearId") UUID academicYearId,
            @Param("direction") PostalDirection direction,
            @Param("postalType") PostalType postalType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            Pageable pageable);

    Optional<PostalRecord> findByIdAndSchoolId(UUID id, UUID schoolId);
}
