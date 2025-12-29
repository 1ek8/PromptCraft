package com.promptcraft.promptcraft.entity;


import com.promptcraft.promptcraft.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @ManyToOne
    User user;

    @ManyToOne
    Plan plan;

    SubscriptionStatus status;

    String stripeCustomerId;

    String stripeSubscriptionId;

    Instant currentPeriodStart;

    Instant currentPeriodEnd;

    Instant createdAt;

    Instant updatedAt;

}
