package com.school.academic.repository;

import com.school.academic.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Student entity.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find student by admission number.
     */
    Optional<Student> findByAdmissionNumber(String admissionNumber);

    /**
     * Check if student exists by admission number.
     */
    boolean existsByAdmissionNumber(String admissionNumber);

    /**
     * Find student by user ID.
     */
    Optional<Student> findByUserId(String userId);

    /**
     * Search students by name (partial match).
     */
    @Query("SELECT s FROM Student s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Student> searchByName(@Param("name") String name);

    /**
     * Find students enrolled in a specific class section for an academic year.
     */
    @Query("SELECT DISTINCT s FROM Student s JOIN Enrollment e ON s.id = e.student.id " +
            "WHERE e.classSection.id = :classSectionId AND e.academicYear.id = :academicYearId")
    List<Student> findByClassSectionAndAcademicYear(
            @Param("classSectionId") Long classSectionId,
            @Param("academicYearId") Long academicYearId);
}
