package com.promptcraft.promptcraft.controller;

import com.promptcraft.promptcraft.dto.subscription.*;
import com.promptcraft.promptcraft.security.AuthUtil;
import com.promptcraft.promptcraft.service.PaymentProcessor;
import com.promptcraft.promptcraft.service.PlanService;
import com.promptcraft.promptcraft.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    private final AuthUtil authUtil;
    private final PaymentProcessor paymentProcessor;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @GetMapping("/api/plans")
    public ResponseEntity<PlanResponse> getAllPlans() {
//        Long userId = 1L;
        return ResponseEntity.ok(planService.getAllActivePlans());
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription() {
//        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(subscriptionService.getCurrentSusbscription());
    }

    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(
            @RequestBody CheckoutRequest request
    ) throws StripeException {
        return ResponseEntity.ok(paymentProcessor.createCheckoutSessionUrl(request));
    }

    @PostMapping("/api/payments/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal() {
//        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(paymentProcessor.openCustomerPortal());
    }

    @PostMapping("/webhooks/payment")
    public ResponseEntity<String> handlePaymentWebhooks(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if(deserializer.getObject().isPresent()) {
                stripeObject = deserializer.getObject().get();
            } else {
                try {
                    stripeObject = deserializer.deserializeUnsafe();
                    if(stripeObject == null) {
                        log.warn("Failed to deserialize webhook object for event: {}", event.getType());
                        return ResponseEntity.ok().build();
                    }
                } catch (Exception e){
                    log.error("Unsafe deserialization failed for event {}: {}", event.getType(), e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialization failed");
                }
            }

            Map<String, String> metadata = new HashMap<>();
            if(stripeObject instanceof Session session){
                metadata = session.getMetadata();
            }

            paymentProcessor.handleWebhookEvent(event.getType(), stripeObject, metadata);

            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }

    }

}
