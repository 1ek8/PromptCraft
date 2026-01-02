package com.promptcraft.promptcraft.repository;

import com.promptcraft.promptcraft.entity.Project;
import com.promptcraft.promptcraft.entity.ProjectParticipant;
import com.promptcraft.promptcraft.entity.ProjectParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<ProjectParticipant, ProjectParticipantId> {

    List<ProjectParticipant> findByIdProjectId(Long projectId);

}
