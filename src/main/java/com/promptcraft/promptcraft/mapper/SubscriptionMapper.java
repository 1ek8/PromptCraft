package com.promptcraft.promptcraft.mapper;

import com.promptcraft.promptcraft.dto.subscription.PlanResponse;
import com.promptcraft.promptcraft.dto.subscription.SubscriptionResponse;
import com.promptcraft.promptcraft.entity.Plan;
import com.promptcraft.promptcraft.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);

}
