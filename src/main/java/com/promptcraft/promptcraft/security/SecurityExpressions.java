package com.promptcraft.promptcraft.security;

import com.promptcraft.promptcraft.entity.enums.ProjectPermission;
import com.promptcraft.promptcraft.entity.enums.ProjectRole;
import com.promptcraft.promptcraft.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityExpressions {

    private final ParticipantRepository participantRepository;
    private final AuthUtil authUtil;

    public boolean hasPermission(Long projectId, ProjectPermission permission){

        Long userId = authUtil.getCurrentUserId();

        return participantRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(ProjectPermission.VIEW))
                .orElse(false);
    }

    public boolean canViewProject(Long projectId) {

        return hasPermission(projectId, ProjectPermission.VIEW);

    }

    public boolean canEditProject(Long projectId) {

        return hasPermission(projectId, ProjectPermission.EDIT);

    }

    public boolean canDeleteProject(Long projectId) {

        return hasPermission(projectId, ProjectPermission.DELETE);

    }

    public boolean canViewMembers(Long projectId) {

        return hasPermission(projectId, ProjectPermission.VIEW_MEMBERS);

    }

    public boolean canManageMembers(Long projectId) {

        return hasPermission(projectId, ProjectPermission.MANAGE_MEMBERS);

    }
}
