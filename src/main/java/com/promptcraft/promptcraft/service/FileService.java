package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.file.FileContentResponse;
import com.promptcraft.promptcraft.dto.file.FileNode;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface FileService {


    List<FileNode> getFileTree(Long projectId, Long userId);

    FileContentResponse getFileContent(Long projectId, String path, Long userId);
}
