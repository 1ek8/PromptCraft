package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.advice.exceptions.ResourceNotFoundException;
import com.promptcraft.promptcraft.dto.subscription.SubscriptionResponse;
import com.promptcraft.promptcraft.entity.Plan;
import com.promptcraft.promptcraft.entity.Subscription;
import com.promptcraft.promptcraft.entity.User;
import com.promptcraft.promptcraft.entity.enums.SubscriptionStatus;
import com.promptcraft.promptcraft.mapper.SubscriptionMapper;
import com.promptcraft.promptcraft.repository.ParticipantRepository;
import com.promptcraft.promptcraft.repository.PlanRepository;
import com.promptcraft.promptcraft.repository.SubscriptionRepository;
import com.promptcraft.promptcraft.repository.UserRepository;
import com.promptcraft.promptcraft.security.AuthUtil;
import com.promptcraft.promptcraft.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final ParticipantRepository participantRepository;
    private final Integer FREE_TIER_PROJECTS_ALLOWED = 1;

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
    @Transactional
    public void updateSubscription(String subId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = getSubscription(subId);
        boolean hasSubscriptionBeenUpdated = false;

        if(status != null && status != subscription.getStatus()){
            subscription.setStatus(status);
            hasSubscriptionBeenUpdated = true;
        }

        //could check for each of the below fields too like i did above, but didn't
        subscription.setCurrentPeriodStart(periodStart);
        subscription.setCurrentPeriodEnd(periodEnd);
        subscription.setCanceledAtPeriodEnd(cancelAtPeriodEnd);
        subscription.setPlan(getPlan(planId));

        if(hasSubscriptionBeenUpdated){
            log.debug("Subscription has been updated: {}", subId);
            subscriptionRepository.save(subscription);
        }
    }

    @Override
    public void cancelSubscription(String subId) {
        Subscription subscription = getSubscription(subId);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
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
        Subscription subscription = getSubscription(subId);
        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            log.debug("Subscription already past due: {}", subId);
            return;
        }

        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

        //TODO: notify user via email/notif
    }

    @Override
    public boolean canCreateNewProject() {

        Long userId = authUtil.getCurrentUserId();
        SubscriptionResponse currentSubscription = getCurrentSusbscription();
        int countOfOwnedProjects = participantRepository.countProjectOwnedByUser(userId);

        if(currentSubscription.plan() == null){
            return countOfOwnedProjects < FREE_TIER_PROJECTS_ALLOWED;
        }

        return countOfOwnedProjects < currentSubscription.plan().maxProjects();
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
