package com.school.academic.repository;

import com.school.academic.domain.StaffAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for StaffAssignment entity.
 */
@Repository
public interface StaffAssignmentRepository extends JpaRepository<StaffAssignment, Long> {

    /**
     * Find staff assignments by staff ID and academic year.
     */
    @Query("SELECT sa FROM StaffAssignment sa " +
            "JOIN FETCH sa.staff " +
            "JOIN FETCH sa.subject " +
            "JOIN FETCH sa.classSection cs " +
            "JOIN FETCH cs.gradeClass " +
            "JOIN FETCH cs.section " +
            "JOIN FETCH sa.academicYear " +
            "WHERE sa.staff.id = :staffId AND sa.academicYear.id = :academicYearId")
    List<StaffAssignment> findByStaffIdAndAcademicYearId(
            @Param("staffId") Long staffId,
            @Param("academicYearId") Long academicYearId);

    /**
     * Find staff assignments by class section and academic year.
     */
    @Query("SELECT sa FROM StaffAssignment sa " +
            "JOIN FETCH sa.staff " +
            "JOIN FETCH sa.subject " +
            "JOIN FETCH sa.classSection " +
            "WHERE sa.classSection.id = :classSectionId AND sa.academicYear.id = :academicYearId")
    List<StaffAssignment> findByClassSectionIdAndAcademicYearId(
            @Param("classSectionId") Long classSectionId,
            @Param("academicYearId") Long academicYearId);

    /**
     * Check if staff assignment exists.
     */
    boolean existsByStaffIdAndSubjectIdAndClassSectionIdAndAcademicYearId(
            Long staffId, Long subjectId, Long classSectionId, Long academicYearId);
}
