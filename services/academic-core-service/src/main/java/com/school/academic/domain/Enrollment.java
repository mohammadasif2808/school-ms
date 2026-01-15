package com.school.academic.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Enrollment entity - The specific placement of a Student in a ClassSection.
 * Roll numbers belong to Enrollment and are unique per (ClassSection, AcademicYear).
 */
@Entity
@Table(name = "enrollment", schema = "academic_core",
        indexes = {
                @Index(name = "ux_enrollment_roll", columnList = "class_section_id, academic_year_id, roll_number", unique = true),
                @Index(name = "ux_enrollment_student_year", columnList = "student_id, academic_year_id", unique = true)
        })
public class Enrollment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_section_id", nullable = false)
    private ClassSection classSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(name = "roll_number")
    private Integer rollNumber;

    @Column(name = "enrollment_status", length = 32)
    private String status = "ACTIVE"; // ACTIVE, PROMOTED, DETAINED, WITHDRAWN

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Default constructor
    public Enrollment() {
    }

    // Builder-style constructor
    public Enrollment(Student student, ClassSection classSection, AcademicYear academicYear,
                      Integer rollNumber, String status, LocalDate enrollmentDate) {
        this.student = student;
        this.classSection = classSection;
        this.academicYear = academicYear;
        this.rollNumber = rollNumber;
        this.status = status;
        this.enrollmentDate = enrollmentDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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

    public Integer getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(Integer rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Check if enrollment is active.
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}
