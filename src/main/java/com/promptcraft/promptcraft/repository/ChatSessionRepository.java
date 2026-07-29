package com.promptcraft.promptcraft.repository;

import com.promptcraft.promptcraft.entity.ChatSession;
import com.promptcraft.promptcraft.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {



}
