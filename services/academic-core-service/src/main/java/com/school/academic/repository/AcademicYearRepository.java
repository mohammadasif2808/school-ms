package com.school.academic.repository;

import com.school.academic.domain.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for AcademicYear entity.
 */
@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {

    /**
     * Find academic year by code.
     */
    Optional<AcademicYear> findByCode(String code);

    /**
     * Check if academic year exists by code.
     */
    boolean existsByCode(String code);

    /**
     * Find the current active academic year.
     */
    Optional<AcademicYear> findByIsActiveTrue();
}
