package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.auth.UserProfileResponse;

public interface UserService {

    UserProfileResponse getProfile(Long userId);
}
