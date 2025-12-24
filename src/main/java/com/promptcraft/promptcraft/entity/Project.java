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
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @Column(nullable = false)
    String name;

    @ManyToOne
    User owner;

    Boolean isPublic = false;

    Instant createdAt;

    Instant updatedAt;

    Instant deletedAt;

}
