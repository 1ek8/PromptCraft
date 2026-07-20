package com.promptcraft.promptcraft.repository;

import com.promptcraft.promptcraft.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<ProjectFile, Long> {

    List<ProjectFile> findByProjectId(Long projectId);

    Optional<ProjectFile> findByProjectIdAndPath(Long projectId, String cleanPath);
}
