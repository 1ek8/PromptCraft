package com.promptcraft.promptcraft.dto.participant;

import com.promptcraft.promptcraft.entity.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateParticipantRole(

        @NotNull
        ProjectRole role
) {
}
