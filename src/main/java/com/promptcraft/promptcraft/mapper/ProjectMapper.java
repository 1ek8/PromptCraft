package com.promptcraft.promptcraft.mapper;

import com.promptcraft.promptcraft.dto.project.ProjectResponse;
import com.promptcraft.promptcraft.dto.project.ProjectSummaryResponse;
import com.promptcraft.promptcraft.entity.Project;
import com.promptcraft.promptcraft.entity.enums.ProjectRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

//    ProjectSummaryResponse toProjectSummaryResponse(Project project);
    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);

    List<ProjectSummaryResponse> toListOfProjectSummaryRepsonse(List<Project> projects);

}
