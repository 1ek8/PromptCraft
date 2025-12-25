package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.subscription.CheckoutRequest;
import com.promptcraft.promptcraft.dto.subscription.CheckoutResponse;
import com.promptcraft.promptcraft.dto.subscription.PortalResponse;
import com.promptcraft.promptcraft.dto.subscription.SubscriptionResponse;

public interface SubscriptionService {

    SubscriptionResponse getCurrentSusbsciption(Long userId);

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId);

    PortalResponse openCustomerPortal(Long userId);
}
