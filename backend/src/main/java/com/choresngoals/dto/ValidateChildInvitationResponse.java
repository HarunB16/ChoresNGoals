package com.choresngoals.dto;

import java.time.Instant;
import java.util.UUID;

import com.choresngoals.entity.ChildInvitationStatus;

public record ValidateChildInvitationResponse(
        UUID id,
        UUID familyId,
        String invitedEmail,
        ChildInvitationStatus status,
        Instant expiresAt,
        Instant createdAt
) {
}
