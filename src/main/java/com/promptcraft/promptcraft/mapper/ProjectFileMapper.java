package com.promptcraft.promptcraft.mapper;

import com.promptcraft.promptcraft.dto.file.FileNode;
import com.promptcraft.promptcraft.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List <ProjectFile> projectFileList);

}
