package com.school.academic.service.impl;

import com.school.academic.domain.Parent;
import com.school.academic.dto.request.CreateParentRequest;
import com.school.academic.dto.response.ParentResponse;
import com.school.academic.repository.ParentRepository;
import com.school.academic.service.ParentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ParentServiceImpl implements ParentService {

    private static final Logger log = LoggerFactory.getLogger(ParentServiceImpl.class);

    private final ParentRepository parentRepository;

    public ParentServiceImpl(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    @Override
    public ParentResponse createParent(CreateParentRequest request) {
        log.info("Creating parent: {} {}", request.getFirstName(), request.getLastName());

        Parent parent = new Parent();
        parent.setUserId(request.getUserId());
        parent.setFirstName(request.getFirstName());
        parent.setLastName(request.getLastName());
        parent.setPhone(request.getMobile());
        parent.setEmail(request.getEmail());
        parent.setRelationship(request.getRelationship());
        parent.setAddress(request.getAddress());

        Parent saved = parentRepository.save(parent);
        log.info("Created parent with id: {}", saved.getId());

        return toParentResponse(saved);
    }

    // ==================== Mappers ====================

    private ParentResponse toParentResponse(Parent entity) {
        ParentResponse response = new ParentResponse();
        response.setId(longToUuid(entity.getId()));
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setMobile(entity.getPhone());
        response.setEmail(entity.getEmail());
        response.setRelationship(entity.getRelationship());
        return response;
    }

    // ==================== Utility Methods ====================

    private UUID longToUuid(Long id) {
        if (id == null) return null;
        return new UUID(0L, id);
    }
}

