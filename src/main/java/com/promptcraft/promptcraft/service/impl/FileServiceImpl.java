package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.advice.exceptions.ResourceNotFoundException;
import com.promptcraft.promptcraft.dto.file.FileContentResponse;
import com.promptcraft.promptcraft.dto.file.FileNode;
import com.promptcraft.promptcraft.entity.Project;
import com.promptcraft.promptcraft.entity.ProjectFile;
import com.promptcraft.promptcraft.mapper.ProjectFileMapper;
import com.promptcraft.promptcraft.mapper.ProjectMapper;
import com.promptcraft.promptcraft.repository.FileRepository;
import com.promptcraft.promptcraft.repository.ProjectRepository;
import com.promptcraft.promptcraft.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;
    private final MinioClient minioClient;
    private final ProjectFileMapper projectFileMapper;

    @Value("${minio.project-bucket}")
    private String projectBucket;

    @Override
    public List<FileNode> getFileTree(Long projectId) {

        List<ProjectFile> projectFileList = fileRepository.findByProjectId(projectId);
        return projectFileMapper.toListOfFileNode(projectFileList);

    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {

//        log.info("Saving file {}", filePath);
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ResourceNotFoundException("Project", projectId.toString())
        );

        String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        String objectKey = projectId + "/" + cleanPath;

        try {
            byte[] contentBytes = fileContent.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(projectBucket)
                            .object(objectKey)
                            .stream(inputStream, contentBytes.length, - 1)
                            .contentType(determineContentType(filePath))
                            .build());

            ProjectFile file = fileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet( () -> ProjectFile.builder()
                            .project(project)
                            .path(cleanPath)
                            .minioObjectKey(objectKey)
                            .createdAt(Instant.now())
                            .build());

            file.setUpdatedAt(Instant.now());
            fileRepository.save(file);

            log.info("Saved file with objectKey : {}", objectKey);

        } catch (Exception e) {
            log.error("Failed to save file : {}/{}", projectId, cleanPath, e);
            throw new RuntimeException("File save failed", e);
        }
    }

    private String determineContentType(String path) {
        String type = URLConnection.guessContentTypeFromName(path);

        if(type == null) return type;
        if (path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".css")) return "text/css";

        return "text/plain";
    }

}
