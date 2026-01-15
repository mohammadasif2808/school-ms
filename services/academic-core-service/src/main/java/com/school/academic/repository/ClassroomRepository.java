package com.school.academic.repository;

import com.school.academic.domain.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Classroom entity.
 */
@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    /**
     * Find classroom by room number.
     */
    Optional<Classroom> findByRoomNumber(String roomNumber);

    /**
     * Check if classroom exists by room number.
     */
    boolean existsByRoomNumber(String roomNumber);
}
