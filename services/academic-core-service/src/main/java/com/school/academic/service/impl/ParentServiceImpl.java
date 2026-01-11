package com.school.academic.service.impl;

import com.school.academic.dto.request.CreateParentRequest;
import com.school.academic.dto.response.ParentResponse;
import com.school.academic.service.ParentService;
import org.springframework.stereotype.Service;

@Service
public class ParentServiceImpl implements ParentService {

    @Override
    public ParentResponse createParent(CreateParentRequest request) {
        // TODO: Implement parent creation logic
        throw new UnsupportedOperationException("Parent creation not yet implemented");
    }
}

