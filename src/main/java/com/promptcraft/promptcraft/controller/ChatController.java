package com.promptcraft.promptcraft.controller;

import com.promptcraft.promptcraft.dto.chat.ChatRequest;
import com.promptcraft.promptcraft.dto.chat.ChatResponse;
import com.promptcraft.promptcraft.dto.chat.StreamResponse;
import com.promptcraft.promptcraft.service.AIGenerationService;
import com.promptcraft.promptcraft.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final AIGenerationService aiGenerationService;
    private final ChatService chatService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @RequestBody ChatRequest request
    ) {
        // Using SseEmitter (native Spring MVC async) instead of returning a raw reactive
        // Flux. Returning a Flux from a Spring MVC controller routes it through
        // ReactiveTypeHandler, whose async re-dispatch re-enters the Spring Security
        // AuthorizationFilter on a thread without authentication. With the SSE response
        // already committed that throws "Unable to handle the Spring Security Exception
        // because the response is already committed" and truncates the stream
        // (ERR_INCOMPLETE_CHUNKED_ENCODING). SseEmitter avoids that while keeping the
        // same SSE wire format for the frontend.
        SseEmitter emitter = new SseEmitter(0L);

        Flux<StreamResponse> stream = aiGenerationService.streamResponse(request.message(), request.projectId());

        stream.subscribe(
                data -> {
                    try {
                        emitter.send(SseEmitter.event().data(data));
                    } catch (IOException | IllegalStateException e) {
                        emitter.completeWithError(e);
                    }
                },
                emitter::completeWithError,
                emitter::complete
        );

        return emitter;
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<ChatResponse>> getChatHistory(
            @PathVariable Long projectId
    ){
        return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));
    }


}
