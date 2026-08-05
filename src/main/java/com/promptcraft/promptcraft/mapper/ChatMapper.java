package com.promptcraft.promptcraft.mapper;

import com.promptcraft.promptcraft.dto.chat.ChatEventResponse;
import com.promptcraft.promptcraft.dto.chat.ChatResponse;
import com.promptcraft.promptcraft.entity.ChatEvent;
import com.promptcraft.promptcraft.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);

    @Mapping(source = "chatEventType", target = "type")
    ChatEventResponse fromChatEvent(ChatEvent chatEvent);

}
