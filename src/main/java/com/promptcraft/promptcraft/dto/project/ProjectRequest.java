package com.promptcraft.promptcraft.dto.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(

        @NotBlank(message = "Input cant be blank")
        String name
) {
}
