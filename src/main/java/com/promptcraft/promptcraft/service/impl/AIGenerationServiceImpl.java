package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.service.AIGenerationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AIGenerationServiceImpl implements AIGenerationService {
    @Override
    public Flux<String> streamResponse(String message, Long aLong) {
        return null;
    }
}
