package com.promptcraft.promptcraft.repository;

import com.promptcraft.promptcraft.entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEventRepository extends JpaRepository<ChatEvent, Long> {

}
