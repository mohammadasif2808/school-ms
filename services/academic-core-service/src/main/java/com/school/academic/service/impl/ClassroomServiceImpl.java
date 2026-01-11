package com.school.academic.service.impl;

import com.school.academic.dto.request.CreateClassroomRequest;
import com.school.academic.dto.response.ClassroomResponse;
import com.school.academic.service.ClassroomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomServiceImpl implements ClassroomService {

    @Override
    public List<ClassroomResponse> listClassrooms() {
        // TODO: Implement classroom listing logic
        throw new UnsupportedOperationException("Classroom listing not yet implemented");
    }

    @Override
    public ClassroomResponse createClassroom(CreateClassroomRequest request) {
        // TODO: Implement classroom creation logic
        throw new UnsupportedOperationException("Classroom creation not yet implemented");
    }
}

