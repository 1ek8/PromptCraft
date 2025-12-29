package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.file.FileContentResponse;
import com.promptcraft.promptcraft.dto.file.FileNode;
import com.promptcraft.promptcraft.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }
}
