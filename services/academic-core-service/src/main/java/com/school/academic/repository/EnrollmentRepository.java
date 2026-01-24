package com.school.academic.repository;

import com.school.academic.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Enrollment entity.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Find enrollments by academic year.
     */
    @Query("SELECT e FROM Enrollment e " +
            "JOIN FETCH e.student " +
            "JOIN FETCH e.classSection cs " +
            "JOIN FETCH cs.gradeClass " +
            "JOIN FETCH cs.section " +
            "JOIN FETCH e.academicYear " +
            "WHERE e.academicYear.id = :academicYearId")
    List<Enrollment> findByAcademicYearId(@Param("academicYearId") Long academicYearId);

    /**
     * Find enrollments by class section and academic year.
     */
    @Query("SELECT e FROM Enrollment e " +
            "JOIN FETCH e.student " +
            "JOIN FETCH e.classSection cs " +
            "JOIN FETCH cs.gradeClass " +
            "JOIN FETCH cs.section " +
            "JOIN FETCH e.academicYear " +
            "WHERE e.classSection.id = :classSectionId AND e.academicYear.id = :academicYearId")
    List<Enrollment> findByClassSectionIdAndAcademicYearId(
            @Param("classSectionId") Long classSectionId,
            @Param("academicYearId") Long academicYearId);

    /**
     * Find enrollment by student and academic year.
     */
    Optional<Enrollment> findByStudentIdAndAcademicYearId(Long studentId, Long academicYearId);

    /**
     * Check if student has enrollment in academic year.
     */
    boolean existsByStudentIdAndAcademicYearId(Long studentId, Long academicYearId);

    /**
     * Check if roll number exists in class section for academic year.
     */
    boolean existsByClassSectionIdAndAcademicYearIdAndRollNumber(Long classSectionId, Long academicYearId, Integer rollNumber);

    /**
     * Find active enrollments by class section and academic year.
     */
    @Query("SELECT e FROM Enrollment e " +
            "JOIN FETCH e.student " +
            "WHERE e.classSection.id = :classSectionId " +
            "AND e.academicYear.id = :academicYearId " +
            "AND e.status = 'ACTIVE'")
    List<Enrollment> findActiveByClassSectionIdAndAcademicYearId(
            @Param("classSectionId") Long classSectionId,
            @Param("academicYearId") Long academicYearId);

    /**
     * Find max roll number in class section for academic year.
     */
    @Query("SELECT MAX(e.rollNumber) FROM Enrollment e " +
            "WHERE e.classSection.id = :classSectionId AND e.academicYear.id = :academicYearId")
    Optional<Integer> findMaxRollNumberByClassSectionAndAcademicYear(
            @Param("classSectionId") Long classSectionId,
            @Param("academicYearId") Long academicYearId);

    /**
     * Find enrollments by multiple student IDs and academic year.
     */
    @Query("SELECT e FROM Enrollment e " +
            "JOIN FETCH e.student " +
            "JOIN FETCH e.classSection cs " +
            "JOIN FETCH cs.gradeClass " +
            "JOIN FETCH cs.section " +
            "WHERE e.student.id IN :studentIds AND e.academicYear.id = :academicYearId")
    List<Enrollment> findByStudentIdsAndAcademicYearId(
            @Param("studentIds") List<Long> studentIds,
            @Param("academicYearId") Long academicYearId);
}
