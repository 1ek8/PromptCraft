package com.promptcraft.promptcraft.repository;

import com.promptcraft.promptcraft.dto.subscription.SubscriptionResponse;
import com.promptcraft.promptcraft.entity.Subscription;
import com.promptcraft.promptcraft.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserIdandStatus(Long userId, Set<SubscriptionStatus> statusSet);

    boolean existsByStripeSubscriptionId(String subscriptionId);

    Optional<Subscription> findByStripeSubscriptionId(String subId);
}
