package com.choresngoals.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choresngoals.dto.ChildResponse;
import com.choresngoals.dto.CreateChildRequest;
import com.choresngoals.security.AuthenticatedUser;
import com.choresngoals.service.ChildManagementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/children")
public class ChildController {

    private final ChildManagementService childManagementService;

    public ChildController(ChildManagementService childManagementService) {
        this.childManagementService = childManagementService;
    }

    @PostMapping
    public ResponseEntity<ChildResponse> createChild(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody CreateChildRequest request
    ) {
        ChildResponse response = childManagementService.createChild(authenticatedUser, request);

        return ResponseEntity
                .created(URI.create("/api/children/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ChildResponse>> listChildren(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.ok(childManagementService.listChildren(authenticatedUser));
    }

    @GetMapping("/{childId}")
    public ResponseEntity<ChildResponse> getChild(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable UUID childId
    ) {
        return ResponseEntity.ok(childManagementService.getChild(authenticatedUser, childId));
    }
}
