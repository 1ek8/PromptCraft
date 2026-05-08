package com.promptcraft.promptcraft.controller;

import com.promptcraft.promptcraft.dto.project.ProjectRequest;
import com.promptcraft.promptcraft.dto.project.ProjectResponse;
import com.promptcraft.promptcraft.dto.project.ProjectSummaryResponse;
import com.promptcraft.promptcraft.security.AuthUtil;
import com.promptcraft.promptcraft.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final AuthUtil authUtil;

    @GetMapping()
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects(){
//        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(projectService.getUserProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
//        Long userId = 1L;
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

//    @PostMapping("/{id}")
    @PostMapping()
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest request) {
//        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody @Valid ProjectRequest request) {
//        Long userId = 1L;
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
//        Long userId = 1L;
        projectService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

}
