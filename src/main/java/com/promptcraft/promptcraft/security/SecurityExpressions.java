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

    public boolean canViewProject(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        return participantRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(ProjectPermission.VIEW))
                .orElse(false);
    }

    public boolean canEditProject(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        return participantRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(ProjectPermission.EDIT))
                .orElse(false);
    }

}
