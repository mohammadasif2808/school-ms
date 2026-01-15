package com.school.academic.domain;

import jakarta.persistence.*;

/**
 * ClassSection entity - The intersection of Class, Section, and AcademicYear.
 * This is the "Active Class" container for students and assignments.
 */
@Entity
@Table(name = "class_section", schema = "academic_core",
        uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "section_id", "academic_year_id"}))
public class ClassSection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private GradeClass gradeClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(name = "medium", length = 32)
    private String medium = "English";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_staff_id")
    private Staff classTeacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    // Default constructor
    public ClassSection() {
    }

    // Builder-style constructor
    public ClassSection(GradeClass gradeClass, Section section, AcademicYear academicYear, String medium) {
        this.gradeClass = gradeClass;
        this.section = section;
        this.academicYear = academicYear;
        this.medium = medium;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GradeClass getGradeClass() {
        return gradeClass;
    }

    public void setGradeClass(GradeClass gradeClass) {
        this.gradeClass = gradeClass;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public Staff getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(Staff classTeacher) {
        this.classTeacher = classTeacher;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    /**
     * Helper method to get display name (e.g., "Grade 5 - A").
     */
    public String getDisplayName() {
        return gradeClass.getName() + " - " + section.getName();
    }
}
