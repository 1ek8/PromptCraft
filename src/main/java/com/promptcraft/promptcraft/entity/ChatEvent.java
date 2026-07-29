package com.promptcraft.promptcraft.entity;

import com.promptcraft.promptcraft.entity.enums.ChatEventType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "chat_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    ChatMessage chatMessage;

    @Column(nullable = false)
    Integer sequenceOrder;

    @Column(columnDefinition = "text")
    String content;

    String filePath;

    @Enumerated(EnumType.STRING)
    ChatEventType chatEventType;

    @Column(columnDefinition = "text")
    String metadata;

}
