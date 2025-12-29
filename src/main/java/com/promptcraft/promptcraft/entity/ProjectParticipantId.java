package com.promptcraft.promptcraft.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ProjectParticipantId {

    Long projectId;
    Long userId;

}
