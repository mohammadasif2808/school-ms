package com.school.academic.service;

import com.school.academic.domain.*;
import com.school.academic.dto.request.CreateEnrollmentRequest;
import com.school.academic.dto.response.EnrollmentResponse;
import com.school.academic.exception.BusinessRuleException;
import com.school.academic.exception.DuplicateResourceException;
import com.school.academic.exception.ResourceNotFoundException;
import com.school.academic.repository.*;
import com.school.academic.service.impl.EnrollmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EnrollmentService.
 * Tests focus on critical invariants:
 * - Student cannot have >1 active enrollment for same academicYear
 * - Roll number unique per (class_section_id, academic_year_id)
 * - Promotion creates NEW enrollments, old ones are CLOSED, not deleted
 */
@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ClassSectionRepository classSectionRepository;
    @Mock
    private AcademicYearRepository academicYearRepository;

    private EnrollmentService enrollmentService;

    // Test data
    private Student testStudent;
    private ClassSection testClassSection;
    private AcademicYear testAcademicYear;
    private GradeClass testGradeClass;
    private Section testSection;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentServiceImpl(
                enrollmentRepository,
                studentRepository,
                classSectionRepository,
                academicYearRepository
        );

        // Setup test entities
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setAdmissionNumber("ADM001");
        testStudent.setFirstName("John");
        testStudent.setLastName("Doe");

        testGradeClass = new GradeClass();
        testGradeClass.setId(1L);
        testGradeClass.setName("Grade 5");
        testGradeClass.setCode("G5");

        testSection = new Section();
        testSection.setId(1L);
        testSection.setName("A");
        testSection.setCode("A");

        testAcademicYear = new AcademicYear();
        testAcademicYear.setId(1L);
        testAcademicYear.setCode("2025-2026");
        testAcademicYear.setName("2025-2026");

        testClassSection = new ClassSection();
        testClassSection.setId(1L);
        testClassSection.setGradeClass(testGradeClass);
        testClassSection.setSection(testSection);
        testClassSection.setAcademicYear(testAcademicYear);
    }

    @Nested
    @DisplayName("Create Enrollment Tests")
    class CreateEnrollmentTests {

        @Test
        @DisplayName("Should create enrollment successfully")
        void createEnrollment_Success() {
            // Given
            CreateEnrollmentRequest request = new CreateEnrollmentRequest();
            request.setStudentId(new UUID(0L, 1L));
            request.setClassSectionId(new UUID(0L, 1L));
            request.setAcademicYearId(new UUID(0L, 1L));

            Enrollment savedEnrollment = new Enrollment();
            savedEnrollment.setId(1L);
            savedEnrollment.setStudent(testStudent);
            savedEnrollment.setClassSection(testClassSection);
            savedEnrollment.setAcademicYear(testAcademicYear);
            savedEnrollment.setStatus("ACTIVE");
            savedEnrollment.setEnrollmentDate(LocalDate.now());

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(classSectionRepository.findByIdWithAssociations(1L)).thenReturn(Optional.of(testClassSection));
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(testAcademicYear));
            when(enrollmentRepository.existsByStudentIdAndAcademicYearId(1L, 1L)).thenReturn(false);
            when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEnrollment);

            // When
            EnrollmentResponse response = enrollmentService.createEnrollment(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStudentName()).isEqualTo("John Doe");
            assertThat(response.getStatus()).isEqualTo("ACTIVE");
            verify(enrollmentRepository).save(any(Enrollment.class));
        }

        @Test
        @DisplayName("Should throw exception when student not found")
        void createEnrollment_StudentNotFound_ThrowsException() {
            // Given
            CreateEnrollmentRequest request = new CreateEnrollmentRequest();
            request.setStudentId(new UUID(0L, 999L));
            request.setClassSectionId(new UUID(0L, 1L));
            request.setAcademicYearId(new UUID(0L, 1L));

            when(studentRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Student not found");

            verify(enrollmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when student already enrolled in academic year")
        void createEnrollment_DuplicateEnrollment_ThrowsException() {
            // Given
            CreateEnrollmentRequest request = new CreateEnrollmentRequest();
            request.setStudentId(new UUID(0L, 1L));
            request.setClassSectionId(new UUID(0L, 1L));
            request.setAcademicYearId(new UUID(0L, 1L));

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(classSectionRepository.findByIdWithAssociations(1L)).thenReturn(Optional.of(testClassSection));
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(testAcademicYear));
            when(enrollmentRepository.existsByStudentIdAndAcademicYearId(1L, 1L)).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already has an enrollment");

            verify(enrollmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when roll number already exists")
        void createEnrollment_DuplicateRollNumber_ThrowsException() {
            // Given
            CreateEnrollmentRequest request = new CreateEnrollmentRequest();
            request.setStudentId(new UUID(0L, 1L));
            request.setClassSectionId(new UUID(0L, 1L));
            request.setAcademicYearId(new UUID(0L, 1L));
            request.setRollNumber("1");

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(classSectionRepository.findByIdWithAssociations(1L)).thenReturn(Optional.of(testClassSection));
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(testAcademicYear));
            when(enrollmentRepository.existsByStudentIdAndAcademicYearId(1L, 1L)).thenReturn(false);
            when(enrollmentRepository.existsByClassSectionIdAndAcademicYearIdAndRollNumber(1L, 1L, 1))
                    .thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("Roll number");

            verify(enrollmentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Roll Number Invariant Tests")
    class RollNumberInvariantTests {

        @Test
        @DisplayName("Roll number must be unique within ClassSection and AcademicYear")
        void rollNumber_UniqueConstraint() {
            // This test validates the invariant:
            // Roll numbers are unique per (ClassSection, AcademicYear)
            // The same roll number CAN exist in different class sections or years

            CreateEnrollmentRequest request = new CreateEnrollmentRequest();
            request.setStudentId(new UUID(0L, 1L));
            request.setClassSectionId(new UUID(0L, 1L));
            request.setAcademicYearId(new UUID(0L, 1L));
            request.setRollNumber("5");

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(classSectionRepository.findByIdWithAssociations(1L)).thenReturn(Optional.of(testClassSection));
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(testAcademicYear));
            when(enrollmentRepository.existsByStudentIdAndAcademicYearId(1L, 1L)).thenReturn(false);
            when(enrollmentRepository.existsByClassSectionIdAndAcademicYearIdAndRollNumber(1L, 1L, 5))
                    .thenReturn(true);

            assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                    .isInstanceOf(DuplicateResourceException.class);
        }
    }

    @Nested
    @DisplayName("One Enrollment Per Academic Year Tests")
    class OneEnrollmentPerYearTests {

        @Test
        @DisplayName("Student cannot have multiple enrollments in same academic year")
        void student_SingleEnrollment_PerAcademicYear() {
            // Given: Student already has enrollment in 2025-2026
            CreateEnrollmentRequest request = new CreateEnrollmentRequest();
            request.setStudentId(new UUID(0L, 1L));
            request.setClassSectionId(new UUID(0L, 2L)); // Different class section
            request.setAcademicYearId(new UUID(0L, 1L)); // Same academic year

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(classSectionRepository.findByIdWithAssociations(2L)).thenReturn(Optional.of(testClassSection));
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(testAcademicYear));
            // Student already enrolled in this academic year
            when(enrollmentRepository.existsByStudentIdAndAcademicYearId(1L, 1L)).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already has an enrollment");
        }
    }
}
