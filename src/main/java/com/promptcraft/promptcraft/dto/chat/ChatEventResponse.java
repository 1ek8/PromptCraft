package com.promptcraft.promptcraft.dto.chat;

import com.promptcraft.promptcraft.entity.ChatMessage;
import com.promptcraft.promptcraft.entity.enums.ChatEventType;

public record ChatEventResponse(

        Long id,

//        ChatMessage chatMessage,

        ChatEventType chatEventType,

        Integer sequenceOrder,

        String content,

        String filePath,

        String metadata
) {
}
