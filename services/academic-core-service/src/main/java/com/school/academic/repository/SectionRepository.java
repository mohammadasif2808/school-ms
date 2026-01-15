package com.school.academic.repository;

import com.school.academic.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Section entity.
 */
@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    /**
     * Find section by code.
     */
    Optional<Section> findByCode(String code);

    /**
     * Check if section exists by code.
     */
    boolean existsByCode(String code);
}
