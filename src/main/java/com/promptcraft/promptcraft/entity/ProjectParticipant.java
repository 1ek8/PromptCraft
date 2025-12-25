package com.promptcraft.promptcraft.entity;

import com.promptcraft.promptcraft.enums.ProjectRole;
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
public class ProjectParticipant {

    @Id
    ProjectParticipantId Id;

    @ManyToOne
    Project project;

    @ManyToOne
    User user;

    ProjectRole projectRole;

    Instant invitedAt;

    Instant acceptedAt;

}
