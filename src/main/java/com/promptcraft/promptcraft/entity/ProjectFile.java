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
public class ProjectFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @OneToOne
    Project project;

    String path;

    String minioObjectKey;

    Instant createdAt;

    Instant updatedAt;

    @OneToOne
    User createdBy;

    @OneToMany
    User updatedBy;
}
