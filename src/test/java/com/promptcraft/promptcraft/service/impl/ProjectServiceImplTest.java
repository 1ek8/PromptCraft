package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.project.ProjectRequest;
import com.promptcraft.promptcraft.dto.project.ProjectResponse;
import com.promptcraft.promptcraft.dto.project.ProjectSummaryResponse;
import com.promptcraft.promptcraft.entity.Project;
import com.promptcraft.promptcraft.entity.ProjectParticipant;
import com.promptcraft.promptcraft.entity.User;
import com.promptcraft.promptcraft.entity.enums.ProjectRole;
import com.promptcraft.promptcraft.mapper.ProjectMapper;
import com.promptcraft.promptcraft.repository.ParticipantRepository;
import com.promptcraft.promptcraft.repository.ProjectRepository;
import com.promptcraft.promptcraft.repository.UserRepository;
import com.promptcraft.promptcraft.security.AuthUtil;
import com.promptcraft.promptcraft.service.ProjectTemplateService;
import com.promptcraft.promptcraft.service.SubscriptionService;
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

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private ProjectTemplateService projectTemplateService;

    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void testCreateProject_Success() {
        Long userId = 1L;
        ProjectRequest request = new ProjectRequest("My New Project");

        User owner = new User();
        owner.setId(userId);

        Project savedProject = Project.builder()
                .Id(1L)
                .name("My New Project")
                .build();

        ProjectResponse response = new ProjectResponse(
                1L, "My New Project", null, null, null
        );

        when(subscriptionService.canCreateNewProject()).thenReturn(true);
        when(authUtil.getCurrentUserId()).thenReturn(userId);
        when(userRepository.getReferenceById(userId)).thenReturn(owner);
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(projectMapper.toProjectResponse(savedProject)).thenReturn(response);

        ProjectResponse result = projectService.createProject(request);

        assertNotNull(result);
        assertEquals("My New Project", result.name());
        verify(projectTemplateService, times(1)).initializeProjectFromTemplate(1L);
        verify(participantRepository, times(1)).save(any(ProjectParticipant.class));
    }

    @Test
    void testGetUserProjects_Success() {
        Long userId = 1L;

        Project p1 = Project.builder().Id(1L).name("Project 1").build();
        Project p2 = Project.builder().Id(2L).name("Project 2").build();

        ProjectRepository.ProjectWithRole pw1 = mock(ProjectRepository.ProjectWithRole.class);
        when(pw1.getProject()).thenReturn(p1);
        when(pw1.getRole()).thenReturn(ProjectRole.OWNER);

        ProjectRepository.ProjectWithRole pw2 = mock(ProjectRepository.ProjectWithRole.class);
        when(pw2.getProject()).thenReturn(p2);
        when(pw2.getRole()).thenReturn(ProjectRole.EDITOR);

        when(authUtil.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.findAllAccessibleByUser(userId)).thenReturn(List.of(pw1, pw2));
        when(projectMapper.toProjectSummaryResponse(p1, ProjectRole.OWNER))
                .thenReturn(new ProjectSummaryResponse(1L, "Project 1", null, null, ProjectRole.OWNER));
        when(projectMapper.toProjectSummaryResponse(p2, ProjectRole.EDITOR))
                .thenReturn(new ProjectSummaryResponse(2L, "Project 2", null, null, ProjectRole.EDITOR));

        List<ProjectSummaryResponse> result = projectService.getUserProjects();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ProjectRole.OWNER, result.get(0).role());
        assertEquals(ProjectRole.EDITOR, result.get(1).role());
        verify(projectRepository, times(1)).findAllAccessibleByUser(userId);
    }

    @Test
    void testGetProjectById_Success() {
        Long userId = 1L;
        Project project = Project.builder().Id(5L).name("My Project").build();

        ProjectRepository.ProjectWithRole pw = mock(ProjectRepository.ProjectWithRole.class);
        when(pw.getProject()).thenReturn(project);
        when(pw.getRole()).thenReturn(ProjectRole.VIEWER);

        when(authUtil.getCurrentUserId()).thenReturn(userId);
        when(projectRepository.findAccessibleProjectByIdWithRole(5L, userId)).thenReturn(Optional.of(pw));
        when(projectMapper.toProjectSummaryResponse(project, ProjectRole.VIEWER))
                .thenReturn(new ProjectSummaryResponse(5L, "My Project", null, null, ProjectRole.VIEWER));

        ProjectSummaryResponse result = projectService.getProjectById(5L);

        assertNotNull(result);
        assertEquals(ProjectRole.VIEWER, result.role());
    }

    @Test
    void testCreateProject_NotAllowedOnPlan() {
        ProjectRequest request = new ProjectRequest("My Project");

        when(subscriptionService.canCreateNewProject()).thenReturn(false);

        assertThrows(Exception.class, () ->
                projectService.createProject(request)
        );
    }
}
