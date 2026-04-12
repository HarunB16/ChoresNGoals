package com.choresngoals.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choresngoals.dto.AuthResponse;
import com.choresngoals.dto.AuthenticatedUserResponse;
import com.choresngoals.dto.LoginRequest;
import com.choresngoals.dto.RegisterParentRequest;
import com.choresngoals.dto.UserResponse;
import com.choresngoals.security.AuthenticatedUser;
import com.choresngoals.service.ParentAuthenticationService;
import com.choresngoals.service.ParentRegistrationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/parents")
public class ParentAuthController {

    private final ParentRegistrationService parentRegistrationService;
    private final ParentAuthenticationService parentAuthenticationService;

    public ParentAuthController(
            ParentRegistrationService parentRegistrationService,
            ParentAuthenticationService parentAuthenticationService
    ) {
        this.parentRegistrationService = parentRegistrationService;
        this.parentAuthenticationService = parentAuthenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerParent(@Valid @RequestBody RegisterParentRequest request) {
        UserResponse response = parentRegistrationService.registerParent(request);

        return ResponseEntity
                .created(URI.create("/api/users/" + response.id()))
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginParent(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(parentAuthenticationService.loginParent(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthenticatedUserResponse> verifyParentAuth(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.ok(new AuthenticatedUserResponse(
                authenticatedUser.id(),
                authenticatedUser.email(),
                authenticatedUser.role()
        ));
    }
}
