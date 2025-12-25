package com.promptcraft.promptcraft.dto.project;

import com.promptcraft.promptcraft.dto.auth.UserProfileResponse;

import java.time.Instant;

public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        UserProfileResponse user
) {
}
