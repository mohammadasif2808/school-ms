package com.school.academic.repository;

import com.school.academic.domain.SubjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for SubjectAssignment entity.
 */
@Repository
public interface SubjectAssignmentRepository extends JpaRepository<SubjectAssignment, Long> {

    /**
     * Find subject assignments by class and academic year.
     */
    @Query("SELECT sa FROM SubjectAssignment sa " +
            "JOIN FETCH sa.subject " +
            "JOIN FETCH sa.gradeClass " +
            "JOIN FETCH sa.academicYear " +
            "LEFT JOIN FETCH sa.section " +
            "WHERE sa.gradeClass.id = :classId AND sa.academicYear.id = :academicYearId")
    List<SubjectAssignment> findByGradeClassIdAndAcademicYearId(
            @Param("classId") Long classId,
            @Param("academicYearId") Long academicYearId);

    /**
     * Check if subject assignment exists for given combination.
     */
    boolean existsBySubjectIdAndGradeClassIdAndAcademicYearIdAndSectionId(
            Long subjectId, Long classId, Long academicYearId, Long sectionId);

    /**
     * Check if subject is assigned to a class for a year (regardless of section).
     */
    @Query("SELECT COUNT(sa) > 0 FROM SubjectAssignment sa " +
            "WHERE sa.subject.id = :subjectId " +
            "AND sa.gradeClass.id = :classId " +
            "AND sa.academicYear.id = :academicYearId")
    boolean existsSubjectAssignmentForClassYear(
            @Param("subjectId") Long subjectId,
            @Param("classId") Long classId,
            @Param("academicYearId") Long academicYearId);
}
