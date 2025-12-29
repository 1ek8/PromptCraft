package com.promptcraft.promptcraft.entity;

import com.promptcraft.promptcraft.entity.enums.MessageRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {

    @Id
    Long Id;

    @ManyToOne
    ChatSession chatSession;

    String content;

    MessageRole role;

    String toolCalls;

    Integer tokensUsed;

    Instant createdAt;

}
