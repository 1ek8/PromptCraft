package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.auth.UserProfileResponse;
import com.promptcraft.promptcraft.dto.subscription.PlanLimitsResponse;
import com.promptcraft.promptcraft.dto.subscription.UsageTodayResponse;
import com.promptcraft.promptcraft.service.UsageService;
import com.promptcraft.promptcraft.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Override
    public UserProfileResponse getProfile(Long userId) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
