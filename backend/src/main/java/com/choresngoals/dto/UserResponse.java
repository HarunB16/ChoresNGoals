package com.choresngoals.dto;

import java.time.Instant;
import java.util.UUID;

import com.choresngoals.entity.UserRole;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        UserRole role,
        Instant createdAt
) {
}
