package com.school.academic.service.impl;

import com.school.academic.domain.Parent;
import com.school.academic.domain.Student;
import com.school.academic.domain.StudentParent;
import com.school.academic.dto.request.CreateStudentRequest;
import com.school.academic.dto.request.LinkGuardianRequest;
import com.school.academic.dto.response.StudentResponse;
import com.school.academic.exception.DuplicateResourceException;
import com.school.academic.exception.ResourceNotFoundException;
import com.school.academic.repository.ParentRepository;
import com.school.academic.repository.StudentParentRepository;
import com.school.academic.repository.StudentRepository;
import com.school.academic.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final StudentParentRepository studentParentRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
                             ParentRepository parentRepository,
                             StudentParentRepository studentParentRepository) {
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.studentParentRepository = studentParentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> listStudents(UUID classId, UUID sectionId, UUID academicYearId, String name) {
        log.debug("Listing students with filters - classId: {}, sectionId: {}, academicYearId: {}, name: {}",
                classId, sectionId, academicYearId, name);

        List<Student> students;

        // If name filter is provided, search by name
        if (name != null && !name.trim().isEmpty()) {
            students = studentRepository.searchByName(name.trim());
        } else {
            // For now, return all students. TODO: Add enrollment-based filtering
            students = studentRepository.findAll();
        }

        return students.stream()
                .map(this::toStudentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponse createStudent(CreateStudentRequest request) {
        log.info("Creating student with admission number: {}", request.getAdmissionNumber());

        // Check for duplicate admission number
        if (studentRepository.existsByAdmissionNumber(request.getAdmissionNumber())) {
            throw new DuplicateResourceException("DUPLICATE_ADMISSION_NUMBER",
                    "Student with admission number '" + request.getAdmissionNumber() + "' already exists");
        }

        Student student = new Student();
        student.setAdmissionNumber(request.getAdmissionNumber());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setDob(request.getDob());
        student.setGender(request.getGender());
        student.setJoiningDate(request.getJoiningDate());
        student.setBloodGroup(request.getBloodGroup());
        student.setAddress(request.getAddress());
        student.setStatus("ACTIVE");

        Student saved = studentRepository.save(student);
        log.info("Created student with id: {}", saved.getId());

        return toStudentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(UUID id) {
        log.debug("Getting student by id: {}", id);

        Long idLong = uuidToLong(id);
        Student student = studentRepository.findById(idLong)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        return toStudentResponse(student);
    }

    @Override
    public void linkGuardian(UUID studentId, LinkGuardianRequest request) {
        log.info("Linking guardian {} to student {}", request.getParentId(), studentId);

        Long studentIdLong = uuidToLong(studentId);
        Long parentIdLong = uuidToLong(request.getParentId());

        Student student = studentRepository.findById(studentIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Parent parent = parentRepository.findById(parentIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + request.getParentId()));

        // Check if link already exists
        if (studentParentRepository.existsByStudentIdAndParentId(studentIdLong, parentIdLong)) {
            throw new DuplicateResourceException("DUPLICATE_GUARDIAN_LINK",
                    "Guardian is already linked to this student");
        }

        StudentParent studentParent = new StudentParent(
                student,
                parent,
                request.getRelationship(),
                request.getIsPrimaryContact() != null ? request.getIsPrimaryContact() : false
        );

        studentParentRepository.save(studentParent);
        log.info("Successfully linked guardian {} to student {}", parentIdLong, studentIdLong);
    }

    // ==================== Mappers ====================

    private StudentResponse toStudentResponse(Student entity) {
        return new StudentResponse(
                longToUuid(entity.getId()),
                entity.getAdmissionNumber(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDob(),
                entity.getGender(),
                entity.getJoiningDate(),
                entity.getStatus(),
                entity.getUserId()
        );
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

