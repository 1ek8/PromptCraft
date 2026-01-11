package com.promptcraft.promptcraft.dto.participant;

import com.promptcraft.promptcraft.entity.enums.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteParticipantRequest(

//        @Email(message = "Input needs to be email")
        @NotBlank(message = "Input cant be blank")
        String username,

        @NotNull
        ProjectRole role
) {
}
