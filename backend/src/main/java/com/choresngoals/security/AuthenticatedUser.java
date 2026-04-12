package com.choresngoals.security;

import java.util.UUID;

import com.choresngoals.entity.UserRole;

public record AuthenticatedUser(
        UUID id,
        String email,
        UserRole role
) {
}
