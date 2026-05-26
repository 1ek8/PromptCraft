package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.subscription.CheckoutRequest;
import com.promptcraft.promptcraft.dto.subscription.CheckoutResponse;
import com.promptcraft.promptcraft.dto.subscription.PortalResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;


public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request) throws StripeException;

    PortalResponse openCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
