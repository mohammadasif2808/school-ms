package com.school.academic.repository;

import com.school.academic.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Parent entity.
 */
@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    /**
     * Find parent by user ID.
     */
    Optional<Parent> findByUserId(String userId);

    /**
     * Find parent by phone number.
     */
    Optional<Parent> findByPhone(String phone);
}
