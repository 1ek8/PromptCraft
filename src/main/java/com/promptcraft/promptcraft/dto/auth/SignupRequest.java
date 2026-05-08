package com.promptcraft.promptcraft.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(

//        @Email(message = "Input needs to be email")
        @NotBlank(message = "Input cant be blank")
        String username,

        @NotBlank
        @Size(min=3, max=30)
        String name,

        @NotBlank
        @Size(min=5)
        String password
) {
}
