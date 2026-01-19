package com.school.academic.frontoffice.repository;

import com.school.academic.frontoffice.entity.HalfDayNotice;
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
 * Repository for HalfDayNotice entity.
 */
@Repository
public interface HalfDayNoticeRepository extends JpaRepository<HalfDayNotice, UUID> {

    @Query("SELECT h FROM HalfDayNotice h WHERE h.schoolId = :schoolId AND h.academicYearId = :academicYearId " +
            "AND (:classId IS NULL OR h.classId = :classId) " +
            "AND (:sectionId IS NULL OR h.sectionId = :sectionId) " +
            "AND (:fromDate IS NULL OR CAST(h.outTime AS LocalDate) >= :fromDate) " +
            "AND (:toDate IS NULL OR CAST(h.outTime AS LocalDate) <= :toDate) " +
            "AND (:studentId IS NULL OR h.studentId = :studentId)")
    Page<HalfDayNotice> findAllWithFilters(
            @Param("schoolId") UUID schoolId,
            @Param("academicYearId") UUID academicYearId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("studentId") Long studentId,
            Pageable pageable);

    Optional<HalfDayNotice> findByIdAndSchoolId(UUID id, UUID schoolId);
}
