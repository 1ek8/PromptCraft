package com.promptcraft.promptcraft.repository;

import com.promptcraft.promptcraft.entity.Project;
import com.promptcraft.promptcraft.entity.ProjectParticipant;
import com.promptcraft.promptcraft.entity.ProjectParticipantId;
import com.promptcraft.promptcraft.entity.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<ProjectParticipant, ProjectParticipantId> {

    List<ProjectParticipant> findByIdProjectId(Long projectId);

    @Query("""
            SELECT pp.projectRole from ProjectParticipant pp
            WHERE pp.id.projectId = :projectId AND pp.id.userId = :userId
            """)
    Optional<ProjectRole> findRoleByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId")Long userId);

    @Query("""
            SELECT COUNT(pm) FROM ProjectParticipant pm
            WHERE pm.id.userId = :userId AND pm.projectRole = 'OWNER'
            """)
    int countProjectOwnedByUser(@Param("userId") Long userId);
}
