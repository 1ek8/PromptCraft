package com.promptcraft.promptcraft.dto.subscription;

public record PlanResponse(
        Long Id,

        String name,

        String stripePriceId,

        Integer maxProjects,

        Integer maxTokensPerDay,

        Integer maxPreviews,

        Boolean active,

        String price
) {
}
