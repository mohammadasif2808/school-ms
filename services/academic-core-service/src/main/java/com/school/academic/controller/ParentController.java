package com.school.academic.controller;

import com.school.academic.dto.request.CreateParentRequest;
import com.school.academic.dto.response.ParentResponse;
import com.school.academic.service.ParentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parents")
@Tag(name = "Parents", description = "Parent/Guardian management")
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @PostMapping
    public ResponseEntity<ParentResponse> createParent(@Valid @RequestBody CreateParentRequest request) {
        ParentResponse response = parentService.createParent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

