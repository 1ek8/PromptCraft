package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.participant.InviteParticipantRequest;
import com.promptcraft.promptcraft.dto.participant.ParticipantResponse;
import com.promptcraft.promptcraft.dto.participant.UpdateParticipantRole;
import com.promptcraft.promptcraft.entity.Project;
import com.promptcraft.promptcraft.entity.ProjectParticipant;
import com.promptcraft.promptcraft.entity.ProjectParticipantId;
import com.promptcraft.promptcraft.entity.User;
import com.promptcraft.promptcraft.mapper.ParticipantMapper;
import com.promptcraft.promptcraft.repository.ParticipantRepository;
import com.promptcraft.promptcraft.repository.ProjectRepository;
import com.promptcraft.promptcraft.repository.UserRepository;
import com.promptcraft.promptcraft.service.ParticipantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    ParticipantRepository participantRepository;
    ProjectRepository projectRepository;
    ParticipantMapper participantMapper;
    private final UserRepository userRepository;

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
    public ParticipantResponse inviteParticipant(Long projectId, InviteParticipantRequest request, Long userId) {
        Project project = projectRepository.findAccessibleProjectById(projectId, userId). orElseThrow();

        if(!project.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Not allowed");
        }

        User invited = userRepository.findByEmail(request.email()).orElseThrow();

        ProjectParticipantId projectParticipantId = new ProjectParticipantId(projectId, invited.getId());

        if(participantRepository.existsById(projectParticipantId)) {
            throw new RuntimeException("User is already a participant in the project");
        }

        ProjectParticipant participant = ProjectParticipant.builder()
                .Id(projectParticipantId)
                .project(project)
                .user(invited)
                .projectRole(request.role())
                .invitedAt(Instant.now())
                .build();

        participantRepository.save(participant);

        return participantMapper.toParticipantResponseFromParticipant(participant);
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
