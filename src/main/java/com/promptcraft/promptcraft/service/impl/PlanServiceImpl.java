package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.subscription.PlanResponse;
import com.promptcraft.promptcraft.entity.Plan;
import com.promptcraft.promptcraft.repository.PlanRepository;
import com.promptcraft.promptcraft.service.PlanService;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public List<PlanResponse> getAllActivePlans() {
        return planRepository.findAll().stream()
                .filter(plan -> Boolean.TRUE.equals(plan.getActive()))
                .map(this::toPlanResponse)
                .toList();
    }

    private PlanResponse toPlanResponse(Plan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getStripePriceId(),
                plan.getMaxProjects(),
                plan.getMaxTokensPerDay(),
                plan.getMaxPreviews(),
                plan.getActive(),
                formatPrice(plan.getStripePriceId())
        );
    }

    /**
     * Resolves a human readable price (e.g. "$19/month") from the Stripe Price object.
     * Returns null if the price is missing or Stripe cannot be reached, so a Stripe
     * outage never breaks the plans endpoint.
     */
    private String formatPrice(String stripePriceId) {
        if (stripePriceId == null || stripePriceId.isBlank()) {
            return null;
        }

        try {
            Price price = Price.retrieve(stripePriceId);

            if (price.getUnitAmount() == null) {
                return null;
            }

            String amount = BigDecimal.valueOf(price.getUnitAmount())
                    .movePointLeft(2)
                    .stripTrailingZeros()
                    .toPlainString();

            String interval = price.getRecurring() != null
                    ? "/" + price.getRecurring().getInterval()
                    : "";

            return "$" + amount + interval;
        } catch (StripeException e) {
            log.warn("Could not resolve Stripe price {}: {}", stripePriceId, e.getMessage());
            return null;
        }
    }
}
