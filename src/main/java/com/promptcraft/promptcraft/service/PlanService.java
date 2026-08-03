package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.subscription.PlanResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface PlanService {
//    PlanResponse getAllActivePlans();
    List<PlanResponse> getAllActivePlans();
}
