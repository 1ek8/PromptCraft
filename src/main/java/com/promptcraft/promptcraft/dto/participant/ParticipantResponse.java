package com.promptcraft.promptcraft.dto.participant;

import com.promptcraft.promptcraft.entity.enums.ProjectRole;

import java.time.Instant;

public record ParticipantResponse(
        Long userId,
        String username,
        String name,
        String avatarUrl,
        ProjectRole role,
        Instant invitedAt
) {
}
