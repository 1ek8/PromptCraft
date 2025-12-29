package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.subscription.PlanLimitsResponse;
import com.promptcraft.promptcraft.dto.subscription.UsageTodayResponse;
import com.promptcraft.promptcraft.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsage(Long userId) {
        return null;
    }

    @Override
    public PlanLimitsResponse getCurrentLimit(Long userId) {
        return null;
    }
}
