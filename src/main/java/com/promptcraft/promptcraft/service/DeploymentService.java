package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.deploy.DeployResponse;

public interface DeploymentService {

    public DeployResponse deploy(Long projectId);
}
