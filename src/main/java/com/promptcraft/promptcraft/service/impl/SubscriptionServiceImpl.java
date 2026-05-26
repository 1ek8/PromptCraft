package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.advice.exceptions.ResourceNotFoundException;
import com.promptcraft.promptcraft.dto.subscription.SubscriptionResponse;
import com.promptcraft.promptcraft.entity.Plan;
import com.promptcraft.promptcraft.entity.Subscription;
import com.promptcraft.promptcraft.entity.User;
import com.promptcraft.promptcraft.entity.enums.SubscriptionStatus;
import com.promptcraft.promptcraft.mapper.SubscriptionMapper;
import com.promptcraft.promptcraft.repository.PlanRepository;
import com.promptcraft.promptcraft.repository.SubscriptionRepository;
import com.promptcraft.promptcraft.repository.UserRepository;
import com.promptcraft.promptcraft.security.AuthUtil;
import com.promptcraft.promptcraft.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    @Override
    public SubscriptionResponse getCurrentSusbscription() {
        Long userId = authUtil.getCurrentUserId();
        var currentSubscription = subscriptionRepository.findByUserIdandStatus(userId, Set.of(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRAILING
        )).orElse(
                new Subscription()
        );

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if(exists) return;

        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .build();

        subscriptionRepository.save(subscription);

    }

    @Override
    public void updateSubscription(String subId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {

    }

    @Override
    public void cancelSubscription(String id) {

    }

    @Override
    public void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd) {

        Subscription subscription = getSubscription(subId);
        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodStart();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

    }


    @Override
    public void markSubscriptionPastDue(String subId) {

    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User ", userId.toString()));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("User ", planId.toString()));
    }

    private Subscription getSubscription(String subId) {
        return subscriptionRepository.findByStripeSubscriptionId(subId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription ", subId));
    }
}
