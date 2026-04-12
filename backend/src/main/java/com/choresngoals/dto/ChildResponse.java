package com.choresngoals.dto;

import java.time.Instant;
import java.util.UUID;

public record ChildResponse(
        UUID id,
        String name,
        Integer birthYear,
        String avatarColor,
        Instant createdAt
) {
}
