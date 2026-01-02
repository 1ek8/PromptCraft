package com.promptcraft.promptcraft.repository;

import com.promptcraft.promptcraft.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    //project_table is the name of the entity in hibernate domain, this query is for the JPA domain
    @Query("""
            SELECT p FROM Project p
            WHERE p.deletedAt IS NULL
            AND p.owner.id = :userId
            ORDER BY p.updatedAt DESC
            """)
    List<Project> findAllProjectsAccessibleByUser(@Param("userId") Long userId);

    @Query("""
            SELECT p from Project p
            LEFT JOIN FETCH p.owner
            WHERE p.id = :projectId
            AND p.deletedAt IS NULL
            AND p.ownerId = :userId
            """)
    Optional<Project> findAccessibleProjectById(@Param("projectId") Long projectId,
                                                @Param("userId") Long userId);
}
