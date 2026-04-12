package com.choresngoals.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choresngoals.dto.RegisterParentRequest;
import com.choresngoals.dto.UserResponse;
import com.choresngoals.service.ParentRegistrationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/parents")
public class ParentAuthController {

    private final ParentRegistrationService parentRegistrationService;

    public ParentAuthController(ParentRegistrationService parentRegistrationService) {
        this.parentRegistrationService = parentRegistrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerParent(@Valid @RequestBody RegisterParentRequest request) {
        UserResponse response = parentRegistrationService.registerParent(request);

        return ResponseEntity
                .created(URI.create("/api/users/" + response.id()))
                .body(response);
    }
}
