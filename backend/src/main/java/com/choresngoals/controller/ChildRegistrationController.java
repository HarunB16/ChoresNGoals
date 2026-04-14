package com.choresngoals.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choresngoals.dto.RegisterChildRequest;
import com.choresngoals.dto.UserResponse;
import com.choresngoals.dto.ValidateChildInvitationResponse;
import com.choresngoals.service.ChildRegistrationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/children")
public class ChildRegistrationController {

    private final ChildRegistrationService childRegistrationService;

    public ChildRegistrationController(ChildRegistrationService childRegistrationService) {
        this.childRegistrationService = childRegistrationService;
    }

    @GetMapping("/invitations/validate")
    public ResponseEntity<ValidateChildInvitationResponse> validateInvitation(
            @RequestParam String token
    ) {
        return ResponseEntity.ok(childRegistrationService.validateInvitation(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerChild(
            @Valid @RequestBody RegisterChildRequest request
    ) {
        UserResponse response = childRegistrationService.registerChild(request);

        return ResponseEntity
                .created(URI.create("/api/users/" + response.id()))
                .body(response);
    }
}
