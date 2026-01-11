package com.promptcraft.promptcraft.dto.auth;

public record UserProfileResponse(
        Long Id,
        String username,
        String name,
        String avatarUrl
) {
}
