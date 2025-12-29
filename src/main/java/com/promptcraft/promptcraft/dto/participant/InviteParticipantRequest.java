package com.promptcraft.promptcraft.dto.participant;

import com.promptcraft.promptcraft.entity.enums.ProjectRole;

public record InviteParticipantRequest(
        String email,
        ProjectRole role
) {
}
