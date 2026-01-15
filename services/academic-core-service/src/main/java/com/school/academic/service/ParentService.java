package com.school.academic.service;

import com.school.academic.dto.request.CreateParentRequest;
import com.school.academic.dto.response.ParentResponse;

public interface ParentService {

    ParentResponse createParent(CreateParentRequest request);
}

