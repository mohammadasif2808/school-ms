package com.school.academic.domain;

import jakarta.persistence.*;

/**
 * SubjectAssignment entity - Defines what a Class studies in a specific Year.
 * If sectionId is null, subject applies to ALL sections of the class.
 * If sectionId is present, subject is limited ONLY to that section.
 */
@Entity
@Table(name = "subject_assignment", schema = "academic_core",
        uniqueConstraints = @UniqueConstraint(columnNames = {"subject_id", "class_id", "academic_year_id", "section_id"}))
public class SubjectAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private GradeClass gradeClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section; // Optional - if null, applies to all sections

    // Default constructor
    public SubjectAssignment() {
    }

    // Builder-style constructor
    public SubjectAssignment(Subject subject, GradeClass gradeClass, AcademicYear academicYear, Section section) {
        this.subject = subject;
        this.gradeClass = gradeClass;
        this.academicYear = academicYear;
        this.section = section;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public GradeClass getGradeClass() {
        return gradeClass;
    }

    public void setGradeClass(GradeClass gradeClass) {
        this.gradeClass = gradeClass;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }
}
