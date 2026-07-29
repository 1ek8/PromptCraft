package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getProjectChatHistory(Long projectId);

}
