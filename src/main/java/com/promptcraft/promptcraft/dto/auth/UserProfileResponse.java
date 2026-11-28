package com.promptcraft.promptcraft.dto.auth;

public record UserProfileResponse(
        Long id,
        String username,
        String name,
        String avatarUrl
) {
}
