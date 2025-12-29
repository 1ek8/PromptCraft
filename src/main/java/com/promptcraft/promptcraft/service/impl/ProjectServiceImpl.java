package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.project.ProjectRequest;
import com.promptcraft.promptcraft.dto.project.ProjectResponse;
import com.promptcraft.promptcraft.dto.project.ProjectSummaryResponse;
import com.promptcraft.promptcraft.entity.User;
import com.promptcraft.promptcraft.repository.ProjectRepository;
import com.promptcraft.promptcraft.repository.UserRepository;
import com.promptcraft.promptcraft.service.ProjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    private final UserRepository userRepository;


    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
        return List.of();
    }

    @Override
    public ProjectResponse getProjectById(Long id, Long userId) {
        return null;
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow();
        return null;
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request, Long userId) {
        return null;
    }

    @Override
    public void softDelete(Long id, Long userId) {

    }
}
