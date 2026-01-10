package com.promptcraft.promptcraft.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @Email(message = "Input needs to be email")
        @NotBlank(message = "Input cant be blank")
        String email,

        @Size(min=5)
        String password
) {

}
