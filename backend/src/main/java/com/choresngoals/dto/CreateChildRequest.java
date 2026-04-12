package com.choresngoals.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateChildRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must be at most 120 characters")
        String name,

        @NotNull(message = "Birth year is required")
        @Min(value = 1900, message = "Birth year must be 1900 or later")
        Integer birthYear,

        @NotBlank(message = "Avatar color is required")
        @Size(max = 30, message = "Avatar color must be at most 30 characters")
        String avatarColor
) {
}
