package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.chat.ChatResponse;
import com.promptcraft.promptcraft.entity.ChatMessage;
import com.promptcraft.promptcraft.entity.ChatSession;
import com.promptcraft.promptcraft.entity.ChatSessionId;
import com.promptcraft.promptcraft.mapper.ChatMapper;
import com.promptcraft.promptcraft.repository.ChatMessageRepository;
import com.promptcraft.promptcraft.repository.ChatSessionRepository;
import com.promptcraft.promptcraft.security.AuthUtil;
import com.promptcraft.promptcraft.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChatServiceImpl implements ChatService {

    ChatMessageRepository chatMessageRepository;
    ChatSessionRepository chatSessionRepository;
    AuthUtil authUtil;
    ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId, userId)
        );

        List<ChatMessage> chatMessageList =  chatMessageRepository.findByChatSession(chatSession);

        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
