package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.advice.exceptions.ResourceNotFoundException;
import com.promptcraft.promptcraft.dto.project.ProjectRequest;
import com.promptcraft.promptcraft.dto.project.ProjectResponse;
import com.promptcraft.promptcraft.dto.project.ProjectSummaryResponse;
import com.promptcraft.promptcraft.entity.Project;
import com.promptcraft.promptcraft.entity.ProjectParticipant;
import com.promptcraft.promptcraft.entity.ProjectParticipantId;
import com.promptcraft.promptcraft.entity.User;
import com.promptcraft.promptcraft.entity.enums.ProjectRole;
import com.promptcraft.promptcraft.mapper.ProjectMapper;
import com.promptcraft.promptcraft.repository.ParticipantRepository;
import com.promptcraft.promptcraft.repository.ProjectRepository;
import com.promptcraft.promptcraft.repository.UserRepository;
import com.promptcraft.promptcraft.service.ProjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;
    private final ParticipantRepository participantRepository;


    @Override
    //this method shouldnt just fetch the list of projects taht the user owns but also the projects where the user is a participant
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
//        return projectRepository.findAllProjectsAccessibleByUser(userId)
//                .stream()
//                .map(project -> projectMapper.toProjectSummaryResponse(project))
//                .collect(Collectors.toList());

        return projectMapper.toListOfProjectSummaryRepsonse(projectRepository.findAllProjectsAccessibleByUser(userId));
    }

    @Override
    public ProjectResponse getProjectById(Long projectId, Long userId) {
        Project project = projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow(
                () -> new ResourceNotFoundException("Project", projectId.toString())
        );
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow();

        Project project = Project.builder()
                .name(request.name())
//                .owner(owner)
                .build();

        project = projectRepository.save(project);

        ProjectParticipantId participantId = new ProjectParticipantId(project.getId(), owner.getId());
        ProjectParticipant participant = ProjectParticipant.builder()
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();

        participantRepository.save(participant);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request, Long userId) {
        Project project = projectRepository.findAccessibleProjectById(id, userId).orElseThrow();

        project.setName(request.name());
        project = projectRepository.save(project);
        // the project variable returned here will have the updatedAt value set to previous value, not latest one, because this entire function logic is @Transactional. when I set the name of the project the Timestamp is not being updated, it gets updated only when the DB write operation persists updated data.

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id, Long userId) {
        Project project = projectRepository.findAccessibleProjectById(id, userId).orElseThrow();

//        if(!project.getOwner().getId().equals(userId)) {
//            throw new RuntimeException("Only a project's owner can delete a probject");
//        }

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);

    }
}
