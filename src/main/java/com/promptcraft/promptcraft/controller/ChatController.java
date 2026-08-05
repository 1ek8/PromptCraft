package com.promptcraft.promptcraft.controller;

import com.promptcraft.promptcraft.dto.chat.ChatRequest;
import com.promptcraft.promptcraft.dto.chat.ChatResponse;
import com.promptcraft.promptcraft.dto.chat.StreamResponse;
import com.promptcraft.promptcraft.service.AIGenerationService;
import com.promptcraft.promptcraft.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final AIGenerationService aiGenerationService;
    private final ChatService chatService;

    @PostMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<ServerSentEvent<String>> streamChat(
    public Flux<ServerSentEvent<StreamResponse>> streamChat(
            @RequestBody ChatRequest request
    ) {
        return aiGenerationService.streamResponse(request.message(), request.projectId())
//                .map(data -> ServerSentEvent.<String>builder()
                .map(data -> ServerSentEvent.<StreamResponse>builder()
                        .data(data)
                        .build());
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<ChatResponse>> getChatHistory(
            @PathVariable Long projectId
    ){
        return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));
    }


}
