package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.participant.InviteParticipantRequest;
import com.promptcraft.promptcraft.dto.participant.ParticipantResponse;
import com.promptcraft.promptcraft.dto.participant.UpdateParticipantRole;
import com.promptcraft.promptcraft.service.ParticipantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantServiceImpl implements ParticipantService {
    @Override
    public List<ParticipantResponse> getAllMembers(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public List<ParticipantResponse> inviteParticipant(Long projectId, InviteParticipantRequest request, Long userId) {
        return List.of();
    }

    @Override
    public ParticipantResponse updateParticipantRole(Long projectId, Long particpantId, UpdateParticipantRole request, Long userId) {
        return null;
    }

    @Override
    public ParticipantResponse deleteParticipant(Long projectId, Long particpantId, Long userId) {
        return null;
    }
}
