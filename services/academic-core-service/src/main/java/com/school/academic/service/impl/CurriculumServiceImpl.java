package com.school.academic.service.impl;

import com.school.academic.domain.*;
import com.school.academic.dto.request.CreateStaffAssignmentRequest;
import com.school.academic.dto.request.CreateSubjectAssignmentRequest;
import com.school.academic.dto.request.CreateSubjectRequest;
import com.school.academic.dto.response.StaffAssignmentResponse;
import com.school.academic.dto.response.SubjectAssignmentResponse;
import com.school.academic.dto.response.SubjectResponse;
import com.school.academic.exception.BusinessRuleException;
import com.school.academic.exception.DuplicateResourceException;
import com.school.academic.exception.ResourceNotFoundException;
import com.school.academic.repository.*;
import com.school.academic.service.CurriculumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CurriculumServiceImpl implements CurriculumService {

    private static final Logger log = LoggerFactory.getLogger(CurriculumServiceImpl.class);

    private final SubjectRepository subjectRepository;
    private final SubjectAssignmentRepository subjectAssignmentRepository;
    private final StaffAssignmentRepository staffAssignmentRepository;
    private final GradeClassRepository gradeClassRepository;
    private final SectionRepository sectionRepository;
    private final AcademicYearRepository academicYearRepository;
    private final StaffRepository staffRepository;
    private final ClassSectionRepository classSectionRepository;

    public CurriculumServiceImpl(SubjectRepository subjectRepository,
                                 SubjectAssignmentRepository subjectAssignmentRepository,
                                 StaffAssignmentRepository staffAssignmentRepository,
                                 GradeClassRepository gradeClassRepository,
                                 SectionRepository sectionRepository,
                                 AcademicYearRepository academicYearRepository,
                                 StaffRepository staffRepository,
                                 ClassSectionRepository classSectionRepository) {
        this.subjectRepository = subjectRepository;
        this.subjectAssignmentRepository = subjectAssignmentRepository;
        this.staffAssignmentRepository = staffAssignmentRepository;
        this.gradeClassRepository = gradeClassRepository;
        this.sectionRepository = sectionRepository;
        this.academicYearRepository = academicYearRepository;
        this.staffRepository = staffRepository;
        this.classSectionRepository = classSectionRepository;
    }

    // ==================== Subjects ====================

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> listSubjects() {
        log.debug("Listing all subjects");
        return subjectRepository.findAll().stream()
                .map(this::toSubjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubjectResponse createSubject(CreateSubjectRequest request) {
        log.info("Creating subject: {}", request.getName());

        if (subjectRepository.existsByCode(request.getSubjectCode())) {
            throw new DuplicateResourceException("DUPLICATE_SUBJECT",
                    "Subject with code '" + request.getSubjectCode() + "' already exists");
        }

        Subject subject = new Subject();
        subject.setCode(request.getSubjectCode());
        subject.setName(request.getName());
        subject.setType(request.getType());
        subject.setIsOptional(request.getIsOptional() != null ? request.getIsOptional() : false);

        Subject saved = subjectRepository.save(subject);
        log.info("Created subject with id: {}", saved.getId());

        return toSubjectResponse(saved);
    }

    // ==================== Subject Assignments ====================

    @Override
    @Transactional(readOnly = true)
    public List<SubjectAssignmentResponse> listSubjectAssignments(UUID academicYearId, UUID classId) {
        log.debug("Listing subject assignments for academicYearId: {}, classId: {}", academicYearId, classId);

        Long yearIdLong = uuidToLong(academicYearId);
        Long classIdLong = uuidToLong(classId);

        List<SubjectAssignment> assignments = subjectAssignmentRepository.findByGradeClassIdAndAcademicYearId(
                classIdLong, yearIdLong);

        return assignments.stream()
                .map(this::toSubjectAssignmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubjectAssignmentResponse createSubjectAssignment(CreateSubjectAssignmentRequest request) {
        log.info("Creating subject assignment for subjectId: {}, classId: {}, academicYearId: {}",
                request.getSubjectId(), request.getClassId(), request.getAcademicYearId());

        Long subjectIdLong = uuidToLong(request.getSubjectId());
        Long classIdLong = uuidToLong(request.getClassId());
        Long yearIdLong = uuidToLong(request.getAcademicYearId());
        Long sectionIdLong = request.getSectionId() != null ? uuidToLong(request.getSectionId()) : null;

        // Validate references
        Subject subject = subjectRepository.findById(subjectIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId()));

        GradeClass gradeClass = gradeClassRepository.findById(classIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));

        AcademicYear academicYear = academicYearRepository.findById(yearIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.getAcademicYearId()));

        Section section = null;
        if (sectionIdLong != null) {
            section = sectionRepository.findById(sectionIdLong)
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));
        }

        // Check for duplicate
        if (subjectAssignmentRepository.existsBySubjectIdAndGradeClassIdAndAcademicYearIdAndSectionId(
                subjectIdLong, classIdLong, yearIdLong, sectionIdLong)) {
            throw new DuplicateResourceException("DUPLICATE_SUBJECT_ASSIGNMENT",
                    "Subject is already assigned to this class for this academic year");
        }

        SubjectAssignment assignment = new SubjectAssignment();
        assignment.setSubject(subject);
        assignment.setGradeClass(gradeClass);
        assignment.setAcademicYear(academicYear);
        assignment.setSection(section);

        SubjectAssignment saved = subjectAssignmentRepository.save(assignment);
        log.info("Created subject assignment with id: {}", saved.getId());

        return toSubjectAssignmentResponse(saved);
    }

    // ==================== Staff Assignments ====================

    @Override
    public StaffAssignmentResponse createStaffAssignment(CreateStaffAssignmentRequest request) {
        log.info("Creating staff assignment for staffId: {}, subjectId: {}, classSectionId: {}",
                request.getStaffId(), request.getSubjectId(), request.getClassSectionId());

        Long staffIdLong = uuidToLong(request.getStaffId());
        Long subjectIdLong = uuidToLong(request.getSubjectId());
        Long classSectionIdLong = uuidToLong(request.getClassSectionId());
        Long yearIdLong = uuidToLong(request.getAcademicYearId());

        // Validate references
        Staff staff = staffRepository.findById(staffIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + request.getStaffId()));

        Subject subject = subjectRepository.findById(subjectIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId()));

        ClassSection classSection = classSectionRepository.findByIdWithAssociations(classSectionIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Class section not found with id: " + request.getClassSectionId()));

        AcademicYear academicYear = academicYearRepository.findById(yearIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.getAcademicYearId()));

        // INVARIANT: Cannot assign staff to teach subject if subject is not mapped to the class for that year
        boolean subjectAssignedToClass = subjectAssignmentRepository.existsSubjectAssignmentForClassYear(
                subjectIdLong, classSection.getGradeClass().getId(), yearIdLong);

        if (!subjectAssignedToClass) {
            throw new BusinessRuleException("SUBJECT_NOT_IN_CURRICULUM",
                    "Subject '" + subject.getName() + "' is not assigned to class '" +
                    classSection.getGradeClass().getName() + "' for this academic year");
        }

        // Check for duplicate
        if (staffAssignmentRepository.existsByStaffIdAndSubjectIdAndClassSectionIdAndAcademicYearId(
                staffIdLong, subjectIdLong, classSectionIdLong, yearIdLong)) {
            throw new DuplicateResourceException("DUPLICATE_STAFF_ASSIGNMENT",
                    "Staff is already assigned to teach this subject in this class section");
        }

        StaffAssignment assignment = new StaffAssignment();
        assignment.setStaff(staff);
        assignment.setSubject(subject);
        assignment.setClassSection(classSection);
        assignment.setAcademicYear(academicYear);

        StaffAssignment saved = staffAssignmentRepository.save(assignment);
        log.info("Created staff assignment with id: {}", saved.getId());

        return toStaffAssignmentResponse(saved);
    }

    // ==================== Mappers ====================

    private SubjectResponse toSubjectResponse(Subject entity) {
        SubjectResponse response = new SubjectResponse();
        response.setId(longToUuid(entity.getId()));
        response.setName(entity.getName());
        response.setSubjectCode(entity.getCode());
        response.setIsOptional(entity.getIsOptional());
        response.setType(entity.getType());
        return response;
    }

    private SubjectAssignmentResponse toSubjectAssignmentResponse(SubjectAssignment entity) {
        SubjectAssignmentResponse response = new SubjectAssignmentResponse();
        response.setId(longToUuid(entity.getId()));
        response.setSubjectName(entity.getSubject().getName());
        response.setClassName(entity.getGradeClass().getName());
        response.setSectionName(entity.getSection() != null ? entity.getSection().getName() : null);
        return response;
    }

    private StaffAssignmentResponse toStaffAssignmentResponse(StaffAssignment entity) {
        StaffAssignmentResponse response = new StaffAssignmentResponse();
        response.setId(longToUuid(entity.getId()));
        response.setStaffName(entity.getStaff().getFullName());
        response.setSubjectName(entity.getSubject().getName());
        response.setClassSectionName(entity.getClassSection().getDisplayName());
        return response;
    }

    // ==================== Utility Methods ====================

    private Long uuidToLong(UUID uuid) {
        if (uuid == null) return null;
        return uuid.getLeastSignificantBits() & Long.MAX_VALUE;
    }

    private UUID longToUuid(Long id) {
        if (id == null) return null;
        return new UUID(0L, id);
    }
}

