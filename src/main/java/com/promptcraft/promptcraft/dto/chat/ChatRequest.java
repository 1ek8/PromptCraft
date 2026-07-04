package com.promptcraft.promptcraft.dto.chat;

public record ChatRequest(
        String message,
        Long projectId
) {
}
