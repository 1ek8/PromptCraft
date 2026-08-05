package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.file.FileContentResponse;
import com.promptcraft.promptcraft.dto.file.FileNode;
import com.promptcraft.promptcraft.dto.file.FileTreeResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface FileService {


//    List<FileNode> getFileTree(Long projectId);
    FileTreeResponse getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
