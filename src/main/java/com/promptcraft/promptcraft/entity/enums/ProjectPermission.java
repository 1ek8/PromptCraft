package com.promptcraft.promptcraft.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProjectPermission {

    VIEW("project:view"),
    EDIT("project:edit"),
    DELETE("project:delete"),

    MANAGE_MEMBERS("project_members:manage_members"),
    VIEW_MEMBERS("project_members:view_members");


    private final String value;
}
