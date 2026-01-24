package com.school.academic.service;

import com.school.academic.domain.AcademicYear;
import com.school.academic.domain.GradeClass;
import com.school.academic.domain.Section;
import com.school.academic.dto.request.CreateAcademicYearRequest;
import com.school.academic.dto.request.CreateClassRequest;
import com.school.academic.dto.request.CreateSectionRequest;
import com.school.academic.dto.response.AcademicYearResponse;
import com.school.academic.dto.response.ClassResponse;
import com.school.academic.dto.response.SectionResponse;
import com.school.academic.exception.DuplicateResourceException;
import com.school.academic.repository.*;
import com.school.academic.service.impl.AcademicStructureServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AcademicStructureService.
 * Tests follow TDD approach: write test first, then implement.
 */
@ExtendWith(MockitoExtension.class)
class AcademicStructureServiceTest {

    @Mock
    private AcademicYearRepository academicYearRepository;
    @Mock
    private GradeClassRepository gradeClassRepository;
    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private ClassSectionRepository classSectionRepository;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private ClassroomRepository classroomRepository;

    private AcademicStructureService academicStructureService;

    @BeforeEach
    void setUp() {
        academicStructureService = new AcademicStructureServiceImpl(
                academicYearRepository,
                gradeClassRepository,
                sectionRepository,
                classSectionRepository,
                staffRepository,
                classroomRepository
        );
    }

    @Nested
    @DisplayName("Academic Year Tests")
    class AcademicYearTests {

        @Test
        @DisplayName("Should create academic year successfully")
        void createAcademicYear_Success() {
            // Given
            CreateAcademicYearRequest request = new CreateAcademicYearRequest(
                    "2025-2026",
                    LocalDate.of(2025, 6, 1),
                    LocalDate.of(2026, 5, 31),
                    true
            );

            AcademicYear savedYear = new AcademicYear();
            savedYear.setId(1L);
            savedYear.setCode("2025-2026");
            savedYear.setName("2025-2026");
            savedYear.setStartDate(LocalDate.of(2025, 6, 1));
            savedYear.setEndDate(LocalDate.of(2026, 5, 31));
            savedYear.setIsActive(true);

            when(academicYearRepository.existsByCode("2025-2026")).thenReturn(false);
            when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(savedYear);

            // When
            AcademicYearResponse response = academicStructureService.createAcademicYear(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("2025-2026");
            assertThat(response.getIsCurrent()).isTrue();
            verify(academicYearRepository).save(any(AcademicYear.class));
        }

        @Test
        @DisplayName("Should throw exception when creating duplicate academic year")
        void createAcademicYear_DuplicateCode_ThrowsException() {
            // Given
            CreateAcademicYearRequest request = new CreateAcademicYearRequest(
                    "2025-2026",
                    LocalDate.of(2025, 6, 1),
                    LocalDate.of(2026, 5, 31),
                    false
            );

            when(academicYearRepository.existsByCode("2025-2026")).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> academicStructureService.createAcademicYear(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already exists");

            verify(academicYearRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should list all academic years")
        void listAcademicYears_Success() {
            // Given
            AcademicYear year1 = new AcademicYear();
            year1.setId(1L);
            year1.setCode("2024-2025");
            year1.setName("2024-2025");
            year1.setIsActive(false);

            AcademicYear year2 = new AcademicYear();
            year2.setId(2L);
            year2.setCode("2025-2026");
            year2.setName("2025-2026");
            year2.setIsActive(true);

            when(academicYearRepository.findAll()).thenReturn(Arrays.asList(year1, year2));

            // When
            List<AcademicYearResponse> result = academicStructureService.listAcademicYears();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(AcademicYearResponse::getName)
                    .containsExactly("2024-2025", "2025-2026");
        }
    }

    @Nested
    @DisplayName("Class (Grade) Tests")
    class ClassTests {

        @Test
        @DisplayName("Should create class successfully")
        void createClass_Success() {
            // Given
            CreateClassRequest request = new CreateClassRequest();
            request.setName("Grade 5");
            request.setLevelOrder(5);
            request.setDescription("Fifth Grade");

            GradeClass savedClass = new GradeClass();
            savedClass.setId(1L);
            savedClass.setCode("GRADE-5");
            savedClass.setName("Grade 5");
            savedClass.setLevelOrder(5);
            savedClass.setDescription("Fifth Grade");

            when(gradeClassRepository.existsByCode("GRADE-5")).thenReturn(false);
            when(gradeClassRepository.save(any(GradeClass.class))).thenReturn(savedClass);

            // When
            ClassResponse response = academicStructureService.createClass(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Grade 5");
            assertThat(response.getLevelOrder()).isEqualTo(5);
        }

        @Test
        @DisplayName("Should throw exception when creating duplicate class")
        void createClass_DuplicateCode_ThrowsException() {
            // Given
            CreateClassRequest request = new CreateClassRequest();
            request.setName("Grade 5");
            request.setLevelOrder(5);

            when(gradeClassRepository.existsByCode("GRADE-5")).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> academicStructureService.createClass(request))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(gradeClassRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should list classes ordered by level")
        void listClasses_OrderedByLevel() {
            // Given
            GradeClass class1 = new GradeClass();
            class1.setId(1L);
            class1.setCode("GRADE-1");
            class1.setName("Grade 1");
            class1.setLevelOrder(1);

            GradeClass class2 = new GradeClass();
            class2.setId(2L);
            class2.setCode("GRADE-5");
            class2.setName("Grade 5");
            class2.setLevelOrder(5);

            when(gradeClassRepository.findAllByOrderByLevelOrderAsc())
                    .thenReturn(Arrays.asList(class1, class2));

            // When
            List<ClassResponse> result = academicStructureService.listClasses();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getLevelOrder()).isEqualTo(1);
            assertThat(result.get(1).getLevelOrder()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("Section Tests")
    class SectionTests {

        @Test
        @DisplayName("Should create section successfully")
        void createSection_Success() {
            // Given
            CreateSectionRequest request = new CreateSectionRequest();
            request.setName("A");

            Section savedSection = new Section();
            savedSection.setId(1L);
            savedSection.setCode("A");
            savedSection.setName("A");

            when(sectionRepository.existsByCode("A")).thenReturn(false);
            when(sectionRepository.save(any(Section.class))).thenReturn(savedSection);

            // When
            SectionResponse response = academicStructureService.createSection(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("A");
        }

        @Test
        @DisplayName("Should throw exception when creating duplicate section")
        void createSection_DuplicateCode_ThrowsException() {
            // Given
            CreateSectionRequest request = new CreateSectionRequest();
            request.setName("A");

            when(sectionRepository.existsByCode("A")).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> academicStructureService.createSection(request))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(sectionRepository, never()).save(any());
        }
    }
}
