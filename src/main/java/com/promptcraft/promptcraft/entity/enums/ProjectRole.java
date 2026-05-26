package com.promptcraft.promptcraft.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.promptcraft.promptcraft.entity.enums.ProjectPermission.*;

@RequiredArgsConstructor
@Getter
public enum ProjectRole {

    OWNER(Set.of(VIEW, EDIT, DELETE, MANAGE_MEMBERS, VIEW_MEMBERS)),

    EDITOR(Set.of(VIEW, EDIT)),

    VIEWER(Set.of(VIEW));

    private final Set<ProjectPermission> permissions;
}
