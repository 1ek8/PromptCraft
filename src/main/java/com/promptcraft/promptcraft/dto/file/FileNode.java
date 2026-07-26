package com.promptcraft.promptcraft.dto.file;

import java.time.Instant;

public record FileNode(
        String path
//        Instant updatedAt
) {

    @Override
    public String toString() {
        return path;
    }
}
