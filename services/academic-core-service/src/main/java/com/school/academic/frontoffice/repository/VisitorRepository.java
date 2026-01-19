package com.school.academic.frontoffice.repository;

import com.school.academic.frontoffice.entity.Visitor;
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
 * Repository for Visitor entity.
 */
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, UUID> {

    @Query("SELECT v FROM Visitor v WHERE v.schoolId = :schoolId AND v.academicYearId = :academicYearId " +
            "AND (:purpose IS NULL OR v.purpose LIKE %:purpose%) " +
            "AND (:fromDate IS NULL OR CAST(v.checkInTime AS LocalDate) >= :fromDate) " +
            "AND (:toDate IS NULL OR CAST(v.checkInTime AS LocalDate) <= :toDate) " +
            "AND (:search IS NULL OR v.visitorName LIKE %:search% OR v.phoneNumber LIKE %:search%) " +
            "AND (:checkedOut IS NULL OR (:checkedOut = true AND v.checkOutTime IS NOT NULL) OR (:checkedOut = false AND v.checkOutTime IS NULL))")
    Page<Visitor> findAllWithFilters(
            @Param("schoolId") UUID schoolId,
            @Param("academicYearId") UUID academicYearId,
            @Param("purpose") String purpose,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search,
            @Param("checkedOut") Boolean checkedOut,
            Pageable pageable);

    Optional<Visitor> findByIdAndSchoolId(UUID id, UUID schoolId);
}
