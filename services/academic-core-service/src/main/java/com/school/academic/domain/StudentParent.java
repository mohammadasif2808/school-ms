package com.school.academic.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * StudentParent entity - Link table for N:M relationship between Student and Parent.
 */
@Entity
@Table(name = "student_parent", schema = "academic_core")
public class StudentParent {

    @EmbeddedId
    private StudentParentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parentId")
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @Column(name = "relationship", length = 64)
    private String relationship;

    @Column(name = "is_primary_contact")
    private Boolean isPrimaryContact = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Default constructor
    public StudentParent() {
    }

    // Constructor
    public StudentParent(Student student, Parent parent, String relationship, Boolean isPrimaryContact) {
        this.student = student;
        this.parent = parent;
        this.relationship = relationship;
        this.isPrimaryContact = isPrimaryContact;
        this.id = new StudentParentId(student.getId(), parent.getId());
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public StudentParentId getId() {
        return id;
    }

    public void setId(StudentParentId id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Boolean getIsPrimaryContact() {
        return isPrimaryContact;
    }

    public void setIsPrimaryContact(Boolean isPrimaryContact) {
        this.isPrimaryContact = isPrimaryContact;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Composite primary key for StudentParent.
     */
    @Embeddable
    public static class StudentParentId implements Serializable {

        @Column(name = "student_id")
        private Long studentId;

        @Column(name = "parent_id")
        private Long parentId;

        public StudentParentId() {
        }

        public StudentParentId(Long studentId, Long parentId) {
            this.studentId = studentId;
            this.parentId = parentId;
        }

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public Long getParentId() {
            return parentId;
        }

        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StudentParentId that = (StudentParentId) o;
            return Objects.equals(studentId, that.studentId) && Objects.equals(parentId, that.parentId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(studentId, parentId);
        }
    }
}
