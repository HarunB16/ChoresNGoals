package com.choresngoals.dto;

import java.util.UUID;

import com.choresngoals.entity.UserRole;

public record AuthenticatedUserResponse(
        UUID id,
        String email,
        UserRole role
) {
}
