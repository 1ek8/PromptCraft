package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.subscription.PlanLimitsResponse;
import com.promptcraft.promptcraft.dto.subscription.PlanResponse;
import com.promptcraft.promptcraft.dto.subscription.SubscriptionResponse;
import com.promptcraft.promptcraft.dto.subscription.UsageTodayResponse;
import com.promptcraft.promptcraft.entity.UsageLog;
import com.promptcraft.promptcraft.repository.UsageLogRepository;
import com.promptcraft.promptcraft.security.AuthUtil;
import com.promptcraft.promptcraft.service.SubscriptionService;
import com.promptcraft.promptcraft.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;
    private final AuthUtil authUtil;
    private final SubscriptionService subscriptionService;

    @Override
    public void recordTokenUsage(Long userId, int actualTokens) {
        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today).
                orElseGet(() -> createNewDailyLog(userId, today));

        todayLog.setTokensUsed(todayLog.getTokensUsed() + actualTokens);
        usageLogRepository.save(todayLog);
    }

    @Override
    public void checkDailyTokensUsage() {

        Long userId = authUtil.getCurrentUserId();
        SubscriptionResponse subscriptionResponse = subscriptionService.getCurrentSusbscription();
        PlanResponse plan = subscriptionResponse.plan();

        if(plan == null || plan.maxTokensPerDay() == null) return;

        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));

        int currentUsage = todayLog.getTokensUsed();
        int limit = plan.maxTokensPerDay();

        if(currentUsage >= limit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Daily limit reached, Upgrade now");
        }
    }

    private UsageLog createNewDailyLog(Long userId, LocalDate date) {
        UsageLog newLog = UsageLog.builder()
                .userId(userId)
                .date(date)
                .tokensUsed(0)
                .build();

        return usageLogRepository.save(newLog);
    }
//    @Override
//    public UsageTodayResponse getTodayUsage(Long userId) {
//        return null;
//    }
//
//    @Override
//    public PlanLimitsResponse getCurrentLimit(Long userId) {
//        return null;
//    }


}
