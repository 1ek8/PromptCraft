package com.promptcraft.promptcraft.dto.auth;

public record LoginRequest(
        String email,
        String password
) {

}
