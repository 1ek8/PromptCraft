package com.promptcraft.promptcraft.llm.tools;

import com.promptcraft.promptcraft.entity.ChatEvent;
import com.promptcraft.promptcraft.entity.ChatMessage;
import com.promptcraft.promptcraft.entity.enums.ChatEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class LlmResponseParser {

    private static final Pattern GENERIC_TAG_PATTERN = Pattern.compile(
            "(<(message|file|tool)([^>]*)>)([\\s\\S]*?)(</\\2)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(
            "(path|args)=\"([^\"]+)\""
    );

    private Map<String, String> extractAttributes(String attributeString) {
        Map<String, String> attributes = new HashMap<>();
        if(attributeString == null) return attributes;

        Matcher matcher = ATTRIBUTE_PATTERN.matcher(attributeString);
        while (matcher.find()) {
            attributes.put(matcher.group(1), matcher.group(2));
        }

        return attributes;
    }

    public List<ChatEvent> parseChatEvents (String fullResponse, ChatMessage parentMessage) {
        List<ChatEvent> events = new ArrayList<>();
        int orderCounter = 1;

        Matcher matcher = GENERIC_TAG_PATTERN.matcher(fullResponse);

        while (matcher.find()){
            String tagName = matcher.group(2).toLowerCase();
            String attributes = matcher.group(3);
            String content = matcher.group(4).trim();

            Map<String, String> attrMap = extractAttributes(attributes);

            ChatEvent.ChatEventBuilder builder = ChatEvent.builder()
                    .chatMessage(parentMessage)
                    .content(content)
                    .sequenceOrder(orderCounter++);

            switch (tagName){
                case "message" -> builder.chatEventType(ChatEventType.MESSAGE);
                case "file" -> {
                    builder.chatEventType(ChatEventType.FILE_EDIT);
                    builder.filePath(attrMap.get("path"));
                    builder.content(null);
                }
                case "tool" -> {
                    builder.chatEventType(ChatEventType.TOOL_LOG);
                    builder.metadata(attrMap.get("args"));
                }
                default -> { continue; }
            }

            events.add(builder.build());
        }

        return events;
    }

}
