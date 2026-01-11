package com.school.academic.service.impl;

import com.school.academic.dto.request.CreateStudentRequest;
import com.school.academic.dto.request.LinkGuardianRequest;
import com.school.academic.dto.response.StudentResponse;
import com.school.academic.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {

    @Override
    public List<StudentResponse> listStudents(UUID classId, UUID sectionId, UUID academicYearId, String name) {
        // TODO: Implement student listing logic
        throw new UnsupportedOperationException("Student listing not yet implemented");
    }

    @Override
    public StudentResponse createStudent(CreateStudentRequest request) {
        // TODO: Implement student creation logic
        throw new UnsupportedOperationException("Student creation not yet implemented");
    }

    @Override
    public StudentResponse getStudentById(UUID id) {
        // TODO: Implement get student by ID logic
        throw new UnsupportedOperationException("Get student by ID not yet implemented");
    }

    @Override
    public void linkGuardian(UUID studentId, LinkGuardianRequest request) {
        // TODO: Implement guardian linking logic
        throw new UnsupportedOperationException("Guardian linking not yet implemented");
    }
}

