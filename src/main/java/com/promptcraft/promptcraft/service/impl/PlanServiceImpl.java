package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.subscription.PlanResponse;
import com.promptcraft.promptcraft.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    @Override
//    public PlanResponse getAllActivePlans() {
//        return null;
//    }
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
