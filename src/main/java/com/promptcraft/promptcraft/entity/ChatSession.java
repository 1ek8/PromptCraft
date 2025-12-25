package com.promptcraft.promptcraft.entity;

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
public class ChatSession {

    @Id
    @OneToOne
    Project project;

    @ManyToOne
    User user;

    String title;

    Instant createdAt;

    Instant updatedAt;

    Instant deletedAt;


}
