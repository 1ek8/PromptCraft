package com.promptcraft.promptcraft.dto.participant;

import com.promptcraft.promptcraft.enums.ProjectRole;

public record InviteParticipantRequest(
        String email,
        ProjectRole role
) {
}
