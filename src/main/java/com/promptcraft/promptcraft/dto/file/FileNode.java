package com.promptcraft.promptcraft.dto.file;

import java.time.Instant;

public record FileNode(
        String path,
        Instant updatedAt
) {
}
