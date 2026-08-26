package com.promptcraft.promptcraft.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // ask OpenRouter to include token usage metadata in the final stream chunk
                .defaultOptions(OpenAiChatOptions.builder().streamUsage(true).build())
                .build();
    }

}
