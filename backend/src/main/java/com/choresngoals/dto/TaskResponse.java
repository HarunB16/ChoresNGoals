package com.choresngoals.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.choresngoals.entity.TaskStatus;
import com.choresngoals.entity.TaskType;

public record TaskResponse(
        UUID id,
        UUID childId,
        String title,
        String description,
        TaskType type,
        TaskStatus status,
        Integer points,
        LocalDate dueDate,
        Instant createdAt
) {
}
