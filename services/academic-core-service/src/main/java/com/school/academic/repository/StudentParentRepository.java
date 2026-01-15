package com.school.academic.repository;

import com.school.academic.domain.StudentParent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for StudentParent entity.
 */
@Repository
public interface StudentParentRepository extends JpaRepository<StudentParent, StudentParent.StudentParentId> {

    /**
     * Find all guardians for a student.
     */
    List<StudentParent> findByStudentId(Long studentId);

    /**
     * Find all students for a parent.
     */
    List<StudentParent> findByParentId(Long parentId);

    /**
     * Check if link exists.
     */
    boolean existsByStudentIdAndParentId(Long studentId, Long parentId);
}
