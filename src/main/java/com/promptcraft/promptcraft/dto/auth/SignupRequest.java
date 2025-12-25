package com.promptcraft.promptcraft.dto.auth;


public record SignupRequest(
        String email,
        String name,
        String password
) {
}
