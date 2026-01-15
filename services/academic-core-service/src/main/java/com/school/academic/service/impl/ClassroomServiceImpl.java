package com.school.academic.service.impl;

import com.school.academic.domain.Classroom;
import com.school.academic.dto.request.CreateClassroomRequest;
import com.school.academic.dto.response.ClassroomResponse;
import com.school.academic.exception.DuplicateResourceException;
import com.school.academic.repository.ClassroomRepository;
import com.school.academic.service.ClassroomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClassroomServiceImpl implements ClassroomService {

    private static final Logger log = LoggerFactory.getLogger(ClassroomServiceImpl.class);

    private final ClassroomRepository classroomRepository;

    public ClassroomServiceImpl(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassroomResponse> listClassrooms() {
        log.debug("Listing all classrooms");
        return classroomRepository.findAll().stream()
                .map(this::toClassroomResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClassroomResponse createClassroom(CreateClassroomRequest request) {
        log.info("Creating classroom with room number: {}", request.getRoomNumber());

        if (classroomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException("DUPLICATE_ROOM_NUMBER",
                    "Classroom with room number '" + request.getRoomNumber() + "' already exists");
        }

        Classroom classroom = new Classroom();
        classroom.setRoomNumber(request.getRoomNumber());
        classroom.setCapacity(request.getCapacity());
        classroom.setInfraType(request.getInfraType());
        classroom.setBuildingBlock(request.getBuildingBlock());
        classroom.setStatus("ACTIVE");

        Classroom saved = classroomRepository.save(classroom);
        log.info("Created classroom with id: {}", saved.getId());

        return toClassroomResponse(saved);
    }

    // ==================== Mappers ====================

    private ClassroomResponse toClassroomResponse(Classroom entity) {
        ClassroomResponse response = new ClassroomResponse();
        response.setId(longToUuid(entity.getId()));
        response.setRoomNumber(entity.getRoomNumber());
        response.setCapacity(entity.getCapacity());
        response.setInfraType(entity.getInfraType());
        response.setBuildingBlock(entity.getBuildingBlock());
        return response;
    }

    // ==================== Utility Methods ====================

    private UUID longToUuid(Long id) {
        if (id == null) return null;
        return new UUID(0L, id);
    }
}

