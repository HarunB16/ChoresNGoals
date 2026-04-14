package com.choresngoals.dto;

import java.time.Instant;
import java.util.UUID;

import com.choresngoals.entity.ChildInvitationStatus;

public record ChildInvitationResponse(
        UUID id,
        UUID familyId,
        String invitedEmail,
        String token,
        ChildInvitationStatus status,
        Instant expiresAt,
        Instant createdAt
) {
}
