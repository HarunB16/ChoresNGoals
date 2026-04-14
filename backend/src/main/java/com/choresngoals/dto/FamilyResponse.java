package com.choresngoals.dto;

import java.time.Instant;
import java.util.UUID;

public record FamilyResponse(
        UUID id,
        String name,
        Instant createdAt
) {
}
