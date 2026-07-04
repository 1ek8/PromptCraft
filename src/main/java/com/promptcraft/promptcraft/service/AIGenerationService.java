package com.promptcraft.promptcraft.service;

import reactor.core.publisher.Flux;

import java.util.Optional;

public interface AIGenerationService {


    Flux<String> streamResponse(String message, Long aLong);
}
