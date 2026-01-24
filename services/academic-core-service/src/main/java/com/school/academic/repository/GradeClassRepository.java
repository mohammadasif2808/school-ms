package com.school.academic.repository;

import com.school.academic.domain.GradeClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for GradeClass entity.
 */
@Repository
public interface GradeClassRepository extends JpaRepository<GradeClass, Long> {

    /**
     * Find class by code.
     */
    Optional<GradeClass> findByCode(String code);

    /**
     * Check if class exists by code.
     */
    boolean existsByCode(String code);

    /**
     * Find all classes ordered by level.
     */
    List<GradeClass> findAllByOrderByLevelOrderAsc();
}
