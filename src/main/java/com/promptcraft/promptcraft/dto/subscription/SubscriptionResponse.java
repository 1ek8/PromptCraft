package com.promptcraft.promptcraft.dto.subscription;

import java.time.Instant;

public record SubscriptionResponse(
        PlanResponse plan,
        String status,
        Instant periodEnd,
        Long tokenUsedThisCycle
) {
}
