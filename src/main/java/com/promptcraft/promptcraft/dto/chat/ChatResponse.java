package com.promptcraft.promptcraft.dto.chat;

import com.promptcraft.promptcraft.entity.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(

        Long id,

//        ChatSession chatSession,

        MessageRole role,

//        List<ChatEvent> events,
        List<ChatEventResponse> events,

        String content,

        Integer tokensUsed,

        Instant createdAt
) {

}
