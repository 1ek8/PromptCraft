package com.promptcraft.promptcraft.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "project_table",
    indexes = {
        @Index(name = "idx_projects_updated_at_desc", columnList = "updatedAt DESC, deletedAt"),
        @Index(name = "idx_projects_deleted_at", columnList = "deletedAt"),
    }
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @Column(nullable = false)
    String name;

    //    aim should be to have single source of truth. if i need to know owner of a project, it can be known by checking the projectParticipant entity
    //join columns always have their indexes auto-made
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    User owner;

    @Builder.Default
    Boolean isPublic = false;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    Instant deletedAt;

}
