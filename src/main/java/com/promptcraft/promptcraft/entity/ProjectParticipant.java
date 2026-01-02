package com.promptcraft.promptcraft.entity;

import com.promptcraft.promptcraft.entity.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "project_participant_table")
public class ProjectParticipant {

    @EmbeddedId
    ProjectParticipantId Id;

    @ManyToOne
    @MapsId("projectId")
    Project project;

    @ManyToOne
    @MapsId("userId")
    User user;

    ProjectRole projectRole;

    Instant invitedAt;

    Instant acceptedAt;

}
