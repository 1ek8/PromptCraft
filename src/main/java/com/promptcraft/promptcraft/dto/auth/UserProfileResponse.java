package com.promptcraft.promptcraft.dto.auth;

public record UserProfileResponse(
        Long Id,
        String email,
        String name,
        String avatarUrl
) {
}
