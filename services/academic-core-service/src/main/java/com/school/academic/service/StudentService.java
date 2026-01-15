package com.school.academic.service;

import com.school.academic.dto.request.CreateStudentRequest;
import com.school.academic.dto.request.LinkGuardianRequest;
import com.school.academic.dto.response.StudentResponse;

import java.util.List;
import java.util.UUID;

public interface StudentService {

    List<StudentResponse> listStudents(UUID classId, UUID sectionId, UUID academicYearId, String name);

    StudentResponse createStudent(CreateStudentRequest request);

    StudentResponse getStudentById(UUID id);

    void linkGuardian(UUID studentId, LinkGuardianRequest request);
}

