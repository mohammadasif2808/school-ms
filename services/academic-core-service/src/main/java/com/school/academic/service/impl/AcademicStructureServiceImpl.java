package com.school.academic.service.impl;

import com.school.academic.domain.*;
import com.school.academic.dto.request.CreateAcademicYearRequest;
import com.school.academic.dto.request.CreateClassRequest;
import com.school.academic.dto.request.CreateClassSectionRequest;
import com.school.academic.dto.request.CreateSectionRequest;
import com.school.academic.dto.request.UpdateClassSectionRequest;
import com.school.academic.dto.response.AcademicYearResponse;
import com.school.academic.dto.response.ClassResponse;
import com.school.academic.dto.response.ClassSectionResponse;
import com.school.academic.dto.response.SectionResponse;
import com.school.academic.exception.DuplicateResourceException;
import com.school.academic.exception.ResourceNotFoundException;
import com.school.academic.repository.*;
import com.school.academic.service.AcademicStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AcademicStructureServiceImpl implements AcademicStructureService {

    private static final Logger log = LoggerFactory.getLogger(AcademicStructureServiceImpl.class);

    private final AcademicYearRepository academicYearRepository;
    private final GradeClassRepository gradeClassRepository;
    private final SectionRepository sectionRepository;
    private final ClassSectionRepository classSectionRepository;
    private final StaffRepository staffRepository;
    private final ClassroomRepository classroomRepository;

    public AcademicStructureServiceImpl(AcademicYearRepository academicYearRepository,
                                        GradeClassRepository gradeClassRepository,
                                        SectionRepository sectionRepository,
                                        ClassSectionRepository classSectionRepository,
                                        StaffRepository staffRepository,
                                        ClassroomRepository classroomRepository) {
        this.academicYearRepository = academicYearRepository;
        this.gradeClassRepository = gradeClassRepository;
        this.sectionRepository = sectionRepository;
        this.classSectionRepository = classSectionRepository;
        this.staffRepository = staffRepository;
        this.classroomRepository = classroomRepository;
    }

    // ==================== Academic Years ====================

    @Override
    @Transactional(readOnly = true)
    public List<AcademicYearResponse> listAcademicYears() {
        log.debug("Listing all academic years");
        return academicYearRepository.findAll().stream()
                .map(this::toAcademicYearResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AcademicYearResponse createAcademicYear(CreateAcademicYearRequest request) {
        log.info("Creating academic year: {}", request.getName());

        // Use name as code if code not specified (e.g., "2025-2026")
        String code = request.getName().trim();

        if (academicYearRepository.existsByCode(code)) {
            throw new DuplicateResourceException("DUPLICATE_ACADEMIC_YEAR",
                    "Academic year with code '" + code + "' already exists");
        }

        AcademicYear academicYear = new AcademicYear();
        academicYear.setCode(code);
        academicYear.setName(request.getName());
        academicYear.setStartDate(request.getStartDate());
        academicYear.setEndDate(request.getEndDate());
        academicYear.setIsActive(request.getIsCurrent() != null ? request.getIsCurrent() : false);

        AcademicYear saved = academicYearRepository.save(academicYear);
        log.info("Created academic year with id: {}", saved.getId());

        return toAcademicYearResponse(saved);
    }

    // ==================== Classes ====================

    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> listClasses() {
        log.debug("Listing all classes");
        return gradeClassRepository.findAllByOrderByLevelOrderAsc().stream()
                .map(this::toClassResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClassResponse createClass(CreateClassRequest request) {
        log.info("Creating class: {}", request.getName());

        // Generate code from name if not provided
        String code = request.getName().replaceAll("\\s+", "-").toUpperCase();

        if (gradeClassRepository.existsByCode(code)) {
            throw new DuplicateResourceException("DUPLICATE_CLASS",
                    "Class with code '" + code + "' already exists");
        }

        GradeClass gradeClass = new GradeClass();
        gradeClass.setCode(code);
        gradeClass.setName(request.getName());
        gradeClass.setLevelOrder(request.getLevelOrder());
        gradeClass.setDescription(request.getDescription());

        GradeClass saved = gradeClassRepository.save(gradeClass);
        log.info("Created class with id: {}", saved.getId());

        return toClassResponse(saved);
    }

    // ==================== Sections ====================

    @Override
    @Transactional(readOnly = true)
    public List<SectionResponse> listSections() {
        log.debug("Listing all sections");
        return sectionRepository.findAll().stream()
                .map(this::toSectionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SectionResponse createSection(CreateSectionRequest request) {
        log.info("Creating section: {}", request.getName());

        String code = request.getName().trim().toUpperCase();

        if (sectionRepository.existsByCode(code)) {
            throw new DuplicateResourceException("DUPLICATE_SECTION",
                    "Section with code '" + code + "' already exists");
        }

        Section section = new Section();
        section.setCode(code);
        section.setName(request.getName());

        Section saved = sectionRepository.save(section);
        log.info("Created section with id: {}", saved.getId());

        return toSectionResponse(saved);
    }

    // ==================== Class Sections ====================

    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionResponse> listClassSections(UUID academicYearId, UUID classId) {
        log.debug("Listing class sections for academicYearId: {}, classId: {}", academicYearId, classId);

        Long yearIdLong = uuidToLong(academicYearId);

        List<ClassSection> classSections;
        if (classId != null) {
            Long classIdLong = uuidToLong(classId);
            classSections = classSectionRepository.findByAcademicYearIdAndGradeClassId(yearIdLong, classIdLong);
        } else {
            classSections = classSectionRepository.findByAcademicYearId(yearIdLong);
        }

        return classSections.stream()
                .map(this::toClassSectionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClassSectionResponse createClassSection(CreateClassSectionRequest request) {
        log.info("Creating class section for classId: {}, sectionId: {}, academicYearId: {}",
                request.getClassId(), request.getSectionId(), request.getAcademicYearId());

        Long classIdLong = uuidToLong(request.getClassId());
        Long sectionIdLong = uuidToLong(request.getSectionId());
        Long yearIdLong = uuidToLong(request.getAcademicYearId());

        // Validate references exist
        GradeClass gradeClass = gradeClassRepository.findById(classIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));

        Section section = sectionRepository.findById(sectionIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));

        AcademicYear academicYear = academicYearRepository.findById(yearIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.getAcademicYearId()));

        // Check for duplicate
        if (classSectionRepository.existsByGradeClassIdAndSectionIdAndAcademicYearId(classIdLong, sectionIdLong, yearIdLong)) {
            throw new DuplicateResourceException("DUPLICATE_CLASS_SECTION",
                    "Class section already exists for this class, section, and academic year combination");
        }

        ClassSection classSection = new ClassSection();
        classSection.setGradeClass(gradeClass);
        classSection.setSection(section);
        classSection.setAcademicYear(academicYear);
        classSection.setMedium(request.getMedium() != null ? request.getMedium() : "English");

        ClassSection saved = classSectionRepository.save(classSection);
        log.info("Created class section with id: {}", saved.getId());

        return toClassSectionResponse(saved);
    }

    @Override
    public ClassSectionResponse updateClassSection(UUID id, UpdateClassSectionRequest request) {
        log.info("Updating class section: {}", id);

        Long idLong = uuidToLong(id);

        ClassSection classSection = classSectionRepository.findByIdWithAssociations(idLong)
                .orElseThrow(() -> new ResourceNotFoundException("Class section not found with id: " + id));

        // Update class teacher if provided
        if (request.getClassTeacherId() != null) {
            Long teacherIdLong = uuidToLong(request.getClassTeacherId());
            Staff teacher = staffRepository.findById(teacherIdLong)
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + request.getClassTeacherId()));
            classSection.setClassTeacher(teacher);
        }

        // Update classroom if provided
        if (request.getClassroomId() != null) {
            Long classroomIdLong = uuidToLong(request.getClassroomId());
            Classroom classroom = classroomRepository.findById(classroomIdLong)
                    .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + request.getClassroomId()));
            classSection.setClassroom(classroom);
        }

        // Update medium if provided
        if (request.getMedium() != null) {
            classSection.setMedium(request.getMedium());
        }

        ClassSection saved = classSectionRepository.save(classSection);
        log.info("Updated class section: {}", saved.getId());

        return toClassSectionResponse(saved);
    }

    // ==================== Mappers ====================

    private AcademicYearResponse toAcademicYearResponse(AcademicYear entity) {
        return new AcademicYearResponse(
                longToUuid(entity.getId()),
                entity.getName(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getIsActive()
        );
    }

    private ClassResponse toClassResponse(GradeClass entity) {
        return new ClassResponse(
                longToUuid(entity.getId()),
                entity.getName(),
                entity.getLevelOrder(),
                entity.getDescription()
        );
    }

    private SectionResponse toSectionResponse(Section entity) {
        return new SectionResponse(
                longToUuid(entity.getId()),
                entity.getName()
        );
    }

    private ClassSectionResponse toClassSectionResponse(ClassSection entity) {
        ClassSectionResponse response = new ClassSectionResponse();
        response.setId(longToUuid(entity.getId()));
        response.setClassName(entity.getGradeClass().getName());
        response.setSectionName(entity.getSection().getName());
        response.setAcademicYear(entity.getAcademicYear().getName());
        response.setMedium(entity.getMedium());

        if (entity.getClassTeacher() != null) {
            response.setClassTeacherId(longToUuid(entity.getClassTeacher().getId()));
        }
        if (entity.getClassroom() != null) {
            response.setClassroomId(longToUuid(entity.getClassroom().getId()));
        }

        return response;
    }

    // ==================== Utility Methods ====================

    /**
     * Convert UUID to Long for database ID.
     * Uses least significant bits for simplicity.
     */
    private Long uuidToLong(UUID uuid) {
        if (uuid == null) return null;
        return uuid.getLeastSignificantBits() & Long.MAX_VALUE;
    }

    /**
     * Convert Long to UUID for API response.
     * Creates a UUID with the long value in least significant bits.
     */
    private UUID longToUuid(Long id) {
        if (id == null) return null;
        return new UUID(0L, id);
    }
}

