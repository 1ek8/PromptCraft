package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.project.ProjectRequest;
import com.promptcraft.promptcraft.dto.project.ProjectResponse;
import com.promptcraft.promptcraft.dto.project.ProjectSummaryResponse;
import com.promptcraft.promptcraft.entity.Project;
import com.promptcraft.promptcraft.entity.User;
import com.promptcraft.promptcraft.mapper.ProjectMapper;
import com.promptcraft.promptcraft.repository.ProjectRepository;
import com.promptcraft.promptcraft.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void testCreateProject_Success() {
        // Setup
        Long userId = 1L;
        ProjectRequest request = new ProjectRequest("My New Project");

        User owner = new User();
        owner.setId(userId);

        Project savedProject = new Project();
        savedProject.setId(1L);
        savedProject.setName("My New Project");
        savedProject.setOwner(owner);

        ProjectResponse response = new ProjectResponse(
                1L, "My New Project", null, null, null
        );

        // Mock behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(projectMapper.toProjectResponse(savedProject)).thenReturn(response);

        // Execute
        ProjectResponse result = projectService.createProject(request, userId);

        // Assert
        assertNotNull(result);
        assertEquals("My New Project", result.name());
        verify(userRepository, times(1)).findById(userId);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testGetUserProjects_Success() {
        // Setup
        Long userId = 1L;
        List<Project> mockProjects = List.of(
                new Project(1L, "Project 1", null, false, null, null, null),
                new Project(2L, "Project 2", null, false, null, null, null)
        );

        List<ProjectSummaryResponse> mockResponses = List.of(
                new ProjectSummaryResponse(1L, "Project 1", null, null),
                new ProjectSummaryResponse(2L, "Project 2", null, null)
        );

        // Mock behavior
        when(projectRepository.findAllProjectsAccessibleByUser(userId))
                .thenReturn(mockProjects);
        when(projectMapper.toListOfProjectSummaryRepsonse(mockProjects))
                .thenReturn(mockResponses);

        // Execute
        List<ProjectSummaryResponse> result = projectService.getUserProjects(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(projectRepository, times(1)).findAllProjectsAccessibleByUser(userId);
    }

    @Test
    void testCreateProject_UserNotFound() {
        // Setup
        Long userId = 999L;
        ProjectRequest request = new ProjectRequest("My Project");

        // Mock behavior
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Execute & Assert - should throw exception
        assertThrows(Exception.class, () ->
                projectService.createProject(request, userId)
        );
    }
}
