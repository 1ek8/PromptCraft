package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.subscription.CheckoutRequest;
import com.promptcraft.promptcraft.dto.subscription.CheckoutResponse;
import com.promptcraft.promptcraft.dto.subscription.PortalResponse;
import com.promptcraft.promptcraft.dto.subscription.SubscriptionResponse;
import com.promptcraft.promptcraft.service.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public SubscriptionResponse getCurrentSusbsciption(Long userId) {
        return null;
    }

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId) {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }
}
