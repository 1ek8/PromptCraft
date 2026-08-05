package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.chat.StreamResponse;
import reactor.core.publisher.Flux;

import java.util.Optional;

public interface AIGenerationService {


//    Flux<String> streamResponse(String message, Long aLong);
    Flux<StreamResponse> streamResponse(String message, Long aLong);
}
