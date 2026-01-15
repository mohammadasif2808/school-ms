package com.school.academic.service.impl;

import com.school.academic.domain.*;
import com.school.academic.dto.request.BulkPromoteRequest;
import com.school.academic.dto.request.CreateEnrollmentRequest;
import com.school.academic.dto.request.UpdateRollNumberRequest;
import com.school.academic.dto.response.EnrollmentResponse;
import com.school.academic.exception.BusinessRuleException;
import com.school.academic.exception.DuplicateResourceException;
import com.school.academic.exception.ResourceNotFoundException;
import com.school.academic.repository.*;
import com.school.academic.service.EnrollmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ClassSectionRepository classSectionRepository;
    private final AcademicYearRepository academicYearRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 ClassSectionRepository classSectionRepository,
                                 AcademicYearRepository academicYearRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.classSectionRepository = classSectionRepository;
        this.academicYearRepository = academicYearRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollments(UUID academicYearId, UUID classId, UUID sectionId) {
        log.debug("Getting enrollments for academicYearId: {}, classId: {}, sectionId: {}",
                academicYearId, classId, sectionId);

        Long yearIdLong = uuidToLong(academicYearId);

        List<Enrollment> enrollments = enrollmentRepository.findByAcademicYearId(yearIdLong);

        // Filter by class if provided
        if (classId != null) {
            Long classIdLong = uuidToLong(classId);
            enrollments = enrollments.stream()
                    .filter(e -> e.getClassSection().getGradeClass().getId().equals(classIdLong))
                    .collect(Collectors.toList());
        }

        // Filter by section if provided
        if (sectionId != null) {
            Long sectionIdLong = uuidToLong(sectionId);
            enrollments = enrollments.stream()
                    .filter(e -> e.getClassSection().getSection().getId().equals(sectionIdLong))
                    .collect(Collectors.toList());
        }

        return enrollments.stream()
                .map(this::toEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {
        log.info("Creating enrollment for studentId: {}, classSectionId: {}, academicYearId: {}",
                request.getStudentId(), request.getClassSectionId(), request.getAcademicYearId());

        Long studentIdLong = uuidToLong(request.getStudentId());
        Long classSectionIdLong = uuidToLong(request.getClassSectionId());
        Long academicYearIdLong = uuidToLong(request.getAcademicYearId());

        // Validate student exists
        Student student = studentRepository.findById(studentIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        // Validate class section exists
        ClassSection classSection = classSectionRepository.findByIdWithAssociations(classSectionIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Class section not found with id: " + request.getClassSectionId()));

        // Validate academic year exists
        AcademicYear academicYear = academicYearRepository.findById(academicYearIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + request.getAcademicYearId()));

        // INVARIANT: Student cannot have >1 active enrollment for same academicYear
        if (enrollmentRepository.existsByStudentIdAndAcademicYearId(studentIdLong, academicYearIdLong)) {
            throw new DuplicateResourceException("DUPLICATE_ENROLLMENT",
                    "Student already has an enrollment for academic year: " + academicYear.getName());
        }

        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setClassSection(classSection);
        enrollment.setAcademicYear(academicYear);
        enrollment.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        enrollment.setEnrollmentDate(LocalDate.now());

        // Set roll number if provided, otherwise leave null (can be assigned later)
        if (request.getRollNumber() != null && !request.getRollNumber().trim().isEmpty()) {
            Integer rollNum = Integer.parseInt(request.getRollNumber());

            // Validate roll number uniqueness
            if (enrollmentRepository.existsByClassSectionIdAndAcademicYearIdAndRollNumber(
                    classSectionIdLong, academicYearIdLong, rollNum)) {
                throw new DuplicateResourceException("DUPLICATE_ROLL_NUMBER",
                        "Roll number " + rollNum + " already exists in this class section for this academic year");
            }
            enrollment.setRollNumber(rollNum);
        }

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Created enrollment with id: {}", saved.getId());

        return toEnrollmentResponse(saved);
    }

    @Override
    public Map<String, String> updateRollNumbers(List<UpdateRollNumberRequest> requests) {
        log.info("Updating roll numbers for {} enrollments", requests.size());

        Map<String, String> result = new HashMap<>();
        int successCount = 0;
        List<String> errors = new ArrayList<>();

        for (UpdateRollNumberRequest request : requests) {
            try {
                Long enrollmentIdLong = uuidToLong(request.getEnrollmentId());

                Enrollment enrollment = enrollmentRepository.findById(enrollmentIdLong)
                        .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + request.getEnrollmentId()));

                Integer newRollNumber = Integer.parseInt(request.getRollNumber());

                // Check uniqueness (excluding current enrollment)
                boolean isDuplicate = enrollmentRepository.existsByClassSectionIdAndAcademicYearIdAndRollNumber(
                        enrollment.getClassSection().getId(),
                        enrollment.getAcademicYear().getId(),
                        newRollNumber);

                // If duplicate exists and it's not the same enrollment
                if (isDuplicate && !enrollment.getRollNumber().equals(newRollNumber)) {
                    errors.add("Roll number " + newRollNumber + " already exists for enrollment " + request.getEnrollmentId());
                    continue;
                }

                enrollment.setRollNumber(newRollNumber);
                enrollmentRepository.save(enrollment);
                successCount++;

            } catch (Exception e) {
                errors.add("Failed to update roll number for enrollment " + request.getEnrollmentId() + ": " + e.getMessage());
            }
        }

        result.put("message", "Updated " + successCount + " roll numbers");
        if (!errors.isEmpty()) {
            result.put("errors", String.join("; ", errors));
        }

        return result;
    }

    @Override
    public Map<String, Object> bulkPromoteStudents(BulkPromoteRequest request) {
        log.info("Bulk promoting students from academicYearId: {} to academicYearId: {}",
                request.getSourceAcademicYearId(), request.getTargetAcademicYearId());

        Long sourceYearIdLong = uuidToLong(request.getSourceAcademicYearId());
        Long targetYearIdLong = uuidToLong(request.getTargetAcademicYearId());
        Long targetClassSectionIdLong = uuidToLong(request.getTargetClassSectionId());

        // Validate academic years exist
        if (!academicYearRepository.existsById(sourceYearIdLong)) {
            throw new ResourceNotFoundException("Source academic year not found");
        }

        AcademicYear targetYear = academicYearRepository.findById(targetYearIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Target academic year not found"));

        // Validate target class section
        ClassSection targetClassSection = classSectionRepository.findByIdWithAssociations(targetClassSectionIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Target class section not found"));

        // Get enrollments to promote
        List<Enrollment> enrollmentsToPromote;

        if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
            // Promote specific students
            List<Long> studentIdLongs = request.getStudentIds().stream()
                    .map(this::uuidToLong)
                    .collect(Collectors.toList());
            enrollmentsToPromote = enrollmentRepository.findByStudentIdsAndAcademicYearId(studentIdLongs, sourceYearIdLong);
        } else if (request.getSourceClassSectionId() != null) {
            // Promote all students from source class section
            Long sourceClassSectionIdLong = uuidToLong(request.getSourceClassSectionId());
            enrollmentsToPromote = enrollmentRepository.findActiveByClassSectionIdAndAcademicYearId(
                    sourceClassSectionIdLong, sourceYearIdLong);
        } else {
            throw new BusinessRuleException("INVALID_PROMOTION_REQUEST",
                    "Either studentIds or sourceClassSectionId must be provided");
        }

        int promotedCount = 0;
        int failedCount = 0;
        List<String> errors = new ArrayList<>();

        String promotionStatus = request.getPromotionStatus() != null ? request.getPromotionStatus() : "PROMOTED";

        for (Enrollment sourceEnrollment : enrollmentsToPromote) {
            try {
                // Check if student already has enrollment in target year
                if (enrollmentRepository.existsByStudentIdAndAcademicYearId(
                        sourceEnrollment.getStudent().getId(), targetYearIdLong)) {
                    errors.add("Student " + sourceEnrollment.getStudent().getAdmissionNumber() +
                            " already has enrollment in target year");
                    failedCount++;
                    continue;
                }

                // Close source enrollment (mark as PROMOTED or DETAINED)
                sourceEnrollment.setStatus(promotionStatus);
                sourceEnrollment.setEndDate(LocalDate.now());
                enrollmentRepository.save(sourceEnrollment);

                // Create new enrollment in target year
                Enrollment newEnrollment = new Enrollment();
                newEnrollment.setStudent(sourceEnrollment.getStudent());
                newEnrollment.setClassSection(targetClassSection);
                newEnrollment.setAcademicYear(targetYear);
                newEnrollment.setStatus("ACTIVE");
                newEnrollment.setEnrollmentDate(LocalDate.now());
                // Roll number will be assigned later

                enrollmentRepository.save(newEnrollment);
                promotedCount++;

            } catch (Exception e) {
                errors.add("Failed to promote student " + sourceEnrollment.getStudent().getAdmissionNumber() +
                        ": " + e.getMessage());
                failedCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("promotedCount", promotedCount);
        result.put("failedCount", failedCount);
        result.put("errors", errors);

        log.info("Bulk promotion complete. Promoted: {}, Failed: {}", promotedCount, failedCount);

        return result;
    }

    // ==================== Mappers ====================

    private EnrollmentResponse toEnrollmentResponse(Enrollment entity) {
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(longToUuid(entity.getId()));
        response.setStudentId(longToUuid(entity.getStudent().getId()));
        response.setStudentName(entity.getStudent().getFullName());
        response.setClassSectionId(longToUuid(entity.getClassSection().getId()));
        response.setRollNumber(entity.getRollNumber() != null ? entity.getRollNumber().toString() : null);
        response.setStatus(entity.getStatus());
        response.setEnrollmentDate(entity.getEnrollmentDate());
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

