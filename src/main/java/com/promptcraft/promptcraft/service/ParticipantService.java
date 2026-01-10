package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.participant.InviteParticipantRequest;
import com.promptcraft.promptcraft.dto.participant.ParticipantResponse;
import com.promptcraft.promptcraft.dto.participant.UpdateParticipantRole;

import java.util.List;

public interface ParticipantService {

    List<ParticipantResponse> getAllMembers(Long projectId, Long userId);

    ParticipantResponse inviteParticipant(Long projectId, InviteParticipantRequest request, Long userId);

    ParticipantResponse updateParticipantRole(Long projectId, Long particpantId, UpdateParticipantRole request, Long userId);

    void removeParticipant(Long projectId, Long particpantId, Long userId);
}
