package com.school.academic.service;

import com.school.academic.dto.request.CreateClassroomRequest;
import com.school.academic.dto.response.ClassroomResponse;

import java.util.List;

public interface ClassroomService {

    List<ClassroomResponse> listClassrooms();

    ClassroomResponse createClassroom(CreateClassroomRequest request);
}

