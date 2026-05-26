package com.promptcraft.promptcraft.entity;


import com.promptcraft.promptcraft.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(nullable = false, name = "plan_id")
    Plan plan;

    @Enumerated(value = EnumType.STRING)
    SubscriptionStatus status;

    String stripeCustomerId;

    String stripeSubscriptionId;

    Instant currentPeriodStart;
    Instant currentPeriodEnd;
    Boolean canceledAtPeriodEnd = false;

    @CreationTimestamp
    Instant createdAt;
    @UpdateTimestamp
    Instant updatedAt;

}
