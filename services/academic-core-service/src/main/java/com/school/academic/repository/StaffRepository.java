package com.school.academic.repository;

import com.school.academic.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Staff entity.
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    /**
     * Find staff by staff code.
     */
    Optional<Staff> findByStaffCode(String staffCode);

    /**
     * Check if staff exists by staff code.
     */
    boolean existsByStaffCode(String staffCode);

    /**
     * Find staff by user ID.
     */
    Optional<Staff> findByUserId(String userId);
}
