package com.choresngoals.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choresngoals.dto.ChildInvitationResponse;
import com.choresngoals.dto.CreateChildInvitationRequest;
import com.choresngoals.security.AuthenticatedUser;
import com.choresngoals.service.ChildInvitationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/families/{familyId}/child-invitations")
public class ChildInvitationController {

    private final ChildInvitationService childInvitationService;

    public ChildInvitationController(ChildInvitationService childInvitationService) {
        this.childInvitationService = childInvitationService;
    }

    @PostMapping
    public ResponseEntity<ChildInvitationResponse> inviteChild(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable UUID familyId,
            @Valid @RequestBody CreateChildInvitationRequest request
    ) {
        ChildInvitationResponse response = childInvitationService.inviteChild(authenticatedUser, familyId, request);

        return ResponseEntity
                .created(URI.create("/api/families/" + familyId + "/child-invitations/" + response.id()))
                .body(response);
    }
}
