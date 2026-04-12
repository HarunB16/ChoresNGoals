package com.choresngoals.dto;

import java.time.LocalDate;

import com.choresngoals.entity.TaskType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 160, message = "Title must be 160 characters or fewer")
        String title,

        @Size(max = 1000, message = "Description must be 1000 characters or fewer")
        String description,

        @NotNull(message = "Type is required")
        TaskType type,

        @NotNull(message = "Points are required")
        @Min(value = 0, message = "Points cannot be negative")
        Integer points,

        @NotNull(message = "Due date is required")
        @FutureOrPresent(message = "Due date cannot be in the past")
        LocalDate dueDate
) {
}
