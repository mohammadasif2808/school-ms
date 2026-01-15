package com.school.academic.repository;

import com.school.academic.domain.ClassSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ClassSection entity.
 */
@Repository
public interface ClassSectionRepository extends JpaRepository<ClassSection, Long> {

    /**
     * Find class sections by academic year.
     */
    @Query("SELECT cs FROM ClassSection cs " +
            "JOIN FETCH cs.gradeClass " +
            "JOIN FETCH cs.section " +
            "JOIN FETCH cs.academicYear " +
            "WHERE cs.academicYear.id = :academicYearId")
    List<ClassSection> findByAcademicYearId(@Param("academicYearId") Long academicYearId);

    /**
     * Find class sections by academic year and class.
     */
    @Query("SELECT cs FROM ClassSection cs " +
            "JOIN FETCH cs.gradeClass " +
            "JOIN FETCH cs.section " +
            "JOIN FETCH cs.academicYear " +
            "WHERE cs.academicYear.id = :academicYearId AND cs.gradeClass.id = :classId")
    List<ClassSection> findByAcademicYearIdAndGradeClassId(
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId);

    /**
     * Check if class section exists for given combination.
     */
    boolean existsByGradeClassIdAndSectionIdAndAcademicYearId(Long classId, Long sectionId, Long academicYearId);

    /**
     * Find specific class section.
     */
    Optional<ClassSection> findByGradeClassIdAndSectionIdAndAcademicYearId(Long classId, Long sectionId, Long academicYearId);

    /**
     * Find class section with all associations loaded.
     */
    @Query("SELECT cs FROM ClassSection cs " +
            "JOIN FETCH cs.gradeClass " +
            "JOIN FETCH cs.section " +
            "JOIN FETCH cs.academicYear " +
            "LEFT JOIN FETCH cs.classTeacher " +
            "LEFT JOIN FETCH cs.classroom " +
            "WHERE cs.id = :id")
    Optional<ClassSection> findByIdWithAssociations(@Param("id") Long id);
}
