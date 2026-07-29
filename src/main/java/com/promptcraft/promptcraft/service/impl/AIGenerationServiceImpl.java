package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.advice.exceptions.ResourceNotFoundException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String message, Long projectId) {

        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(fileService, projectId);

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
                    fullResponseBuffer.append(content);
                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() -> {
//                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
                        finalizeChats(message, chatSession, fullResponseBuffer.toString(), projectId);
                    });
                })
                .doOnError( error -> {
                    log.error("Error during streaming for projectId: {}", projectId);
                })
                .map(response -> Objects.requireNonNull(response.getResult().getOutput().getText()));
    }

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long projectId) {

        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .build()
        );

        ChatMessage assistantChatMessage = ChatMessage.builder()
                .role(MessageRole.ASSISTANT)
                .chatSession(chatSession)
                .build();

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);

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
