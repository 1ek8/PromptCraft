package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.subscription.PlanLimitsResponse;
import com.promptcraft.promptcraft.dto.subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {

    UsageTodayResponse getTodayUsage(Long userId);

    PlanLimitsResponse getCurrentLimit(Long userId);
}
