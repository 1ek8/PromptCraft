package com.promptcraft.promptcraft.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProjectPermission {

    VIEW("project:view"),

    EDIT("project:edit"),

    DELETE("project:delete"),

    MANAGE_MEMBERS("project:manage_members");


    private final String value;
}
