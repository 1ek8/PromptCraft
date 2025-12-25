package com.promptcraft.promptcraft.dto.participant;

import com.promptcraft.promptcraft.enums.ProjectRole;

import java.time.Instant;

public record ParticipantResponse(
        Long Id,
        String email,
        String name,
        String avatarUrl,
        ProjectRole role,
        Instant invitedAt
) {
}
