package com.school.academic.domain;

import jakarta.persistence.*;

/**
 * StaffAssignment entity - Defines who teaches what, where, and when.
 * One assignment represents One Subject taught to One ClassSection in One AcademicYear.
 * A Staff member can have MULTIPLE assignments.
 */
@Entity
@Table(name = "staff_assignment", schema = "academic_core",
        uniqueConstraints = @UniqueConstraint(columnNames = {"staff_id", "subject_id", "class_section_id", "academic_year_id"}))
public class StaffAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_section_id", nullable = false)
    private ClassSection classSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    // Default constructor
    public StaffAssignment() {
    }

    // Builder-style constructor
    public StaffAssignment(Staff staff, Subject subject, ClassSection classSection, AcademicYear academicYear) {
        this.staff = staff;
        this.subject = subject;
        this.classSection = classSection;
        this.academicYear = academicYear;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public ClassSection getClassSection() {
        return classSection;
    }

    public void setClassSection(ClassSection classSection) {
        this.classSection = classSection;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
    }
}
