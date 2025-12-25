package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.subscription.PlanResponse;
import org.jspecify.annotations.Nullable;

public interface PlanService {
    PlanResponse getAllActivePlans();
}
