package com.school.academic.repository;

import com.school.academic.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Subject entity.
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * Find subject by code.
     */
    Optional<Subject> findByCode(String code);

    /**
     * Check if subject exists by code.
     */
    boolean existsByCode(String code);
}
