package com.promptcraft.promptcraft.mapper;

import com.promptcraft.promptcraft.dto.project.ProjectResponse;
import com.promptcraft.promptcraft.entity.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

}
