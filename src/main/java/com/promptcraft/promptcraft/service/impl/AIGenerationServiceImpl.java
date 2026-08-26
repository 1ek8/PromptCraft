package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.advice.exceptions.ResourceNotFoundException;
import com.promptcraft.promptcraft.dto.chat.StreamResponse;
import com.promptcraft.promptcraft.entity.*;
import com.promptcraft.promptcraft.entity.enums.ChatEventType;
import com.promptcraft.promptcraft.entity.enums.MessageRole;
import com.promptcraft.promptcraft.llm.Prompt;
import com.promptcraft.promptcraft.llm.advisors.FileTreeContextAdvisor;
import com.promptcraft.promptcraft.llm.tools.CodeGenerationTools;
import com.promptcraft.promptcraft.llm.tools.LlmResponseParser;
import com.promptcraft.promptcraft.repository.*;
import com.promptcraft.promptcraft.security.AuthUtil;
import com.promptcraft.promptcraft.service.AIGenerationService;
import com.promptcraft.promptcraft.service.FileService;
import com.promptcraft.promptcraft.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.SystemColor.text;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIGenerationServiceImpl implements AIGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final FileService fileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LlmResponseParser llmResponseParser;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;
    private final UsageLogRepository usageLogRepository;
    private final UsageService usageService;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
//    public Flux<String> streamResponse(String message, Long projectId) {
    public Flux<StreamResponse> streamResponse(String message, Long projectId) {

        usageService.checkDailyTokensUsage();

        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();
        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(fileService, projectId);

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
//        AtomicReference<Long> endTime = new AtomicReference<>(8L);
        AtomicReference<Long> endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();

        return chatClient.prompt()
                .system(Prompt.CODE_GENERATOR_SYSTEM_PROMPT + "---file_tree---" + fileService.getFileTree(projectId).toString())
                .user(message)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                        advisorSpec.params(advisorParams);
                        advisorSpec.advisors(fileTreeContextAdvisor);
                    }
                )
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    String content = response.getResult().getOutput().getText();

                    if(content != null && !content.isEmpty() && endTime.get() == 0){
                        endTime.set(System.currentTimeMillis());
                    }

                    if(response.getMetadata().getUsage() != null){
                        usageRef.set(response.getMetadata().getUsage() );
                    }

                    fullResponseBuffer.append(content);
                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() -> {
//                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
                        long duration = (endTime.get() - startTime.get())/1000;
                        finalizeChats(message, chatSession, fullResponseBuffer.toString(), duration, usageRef.get(), userId);
                    });
                })
                .doOnError( error -> {
                    log.error("Error during streaming for projectId: {}", projectId);
                })
//                .map(response -> Objects.requireNonNull(response.getResult().getOutput().getText()));
                .map(response -> {
                    String text = response.getResult().getOutput().getText();
                    return new StreamResponse(text != null ? text : "");
                    }
                );
    }

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration, Usage usage, Long userId) {

        Long projectId = chatSession.getProject().getId();

        if(usage != null) {
            int totalTokens = usage.getTotalTokens();
            usageService.recordTokenUsage(userId, totalTokens);
        }

        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()
        );

        ChatMessage assistantChatMessage = ChatMessage.builder()
                .role(MessageRole.ASSISTANT)
                .content("Assistant Message here . . .")
                .chatSession(chatSession)
                .tokensUsed(usage.getCompletionTokens())
                .build();

        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
        chatEventList.addFirst(ChatEvent.builder()
                        .chatEventType(ChatEventType.THOUGHT)
                        .chatMessage(assistantChatMessage)
                        .content("Thought for " + duration + "s")
                        .sequenceOrder(0)
                .build());

        chatEventList.stream()
                .filter(e -> e.getChatEventType() == ChatEventType.FILE_EDIT)
                .forEach(e -> fileService.saveFile(projectId, e.getFilePath(), e.getContent()));

        chatEventRepository.saveAll(chatEventList);
    }

    private void parseAndSaveFiles(String fullResponse, Long projectId) {

        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);

        while(matcher.find()) {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();

            fileService.saveFile(projectId, filePath, fileContent);
        }

    }

    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if(chatSession == null) {
            Project project = projectRepository.findById(projectId).orElseThrow(
                    () -> new ResourceNotFoundException("project", projectId.toString())
            );

            User user = userRepository.findById(userId).orElseThrow(
                    () -> new ResourceNotFoundException("user", userId.toString())
            );

            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();

            chatSession = chatSessionRepository.save(chatSession);
        }

        return chatSession;
    }
}
