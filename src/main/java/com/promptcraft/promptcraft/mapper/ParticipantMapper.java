package com.promptcraft.promptcraft.mapper;

import com.promptcraft.promptcraft.dto.participant.ParticipantResponse;
import com.promptcraft.promptcraft.entity.ProjectParticipant;
import com.promptcraft.promptcraft.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "role", constant = "OWNER")
    ParticipantResponse toParticipantResponseFromOwner(User owner);

    @Mapping(target = "userId", source = "user.id") //nested mapping
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "name", source = "user.name")
    ParticipantResponse toParticipantResponseFromParticipant(ProjectParticipant projectParticipant);

}
