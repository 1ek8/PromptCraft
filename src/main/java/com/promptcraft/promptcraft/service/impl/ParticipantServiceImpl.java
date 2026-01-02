package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.participant.InviteParticipantRequest;
import com.promptcraft.promptcraft.dto.participant.ParticipantResponse;
import com.promptcraft.promptcraft.dto.participant.UpdateParticipantRole;
import com.promptcraft.promptcraft.entity.Project;
import com.promptcraft.promptcraft.mapper.ParticipantMapper;
import com.promptcraft.promptcraft.repository.ParticipantRepository;
import com.promptcraft.promptcraft.repository.ProjectRepository;
import com.promptcraft.promptcraft.service.ParticipantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    ParticipantRepository participantRepository;
    ProjectRepository projectRepository;
    ParticipantMapper participantMapper;

    @Override
    public List<ParticipantResponse> getAllMembers(Long projectId, Long userId) {

        Project project = projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow();

        List<ParticipantResponse> memberRepsonseList = new ArrayList<>();
        memberRepsonseList.add(participantMapper.toParticipantResponseFromOwner(project.getOwner()));

        memberRepsonseList.addAll(participantRepository.findByIdProjectId(projectId)
                .stream()
                .map(participant -> participantMapper.toParticipantResponseFromParticipant(participant))
                .toList());


        return memberRepsonseList;
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
