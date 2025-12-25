package com.promptcraft.promptcraft.service;


import com.promptcraft.promptcraft.dto.auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface UserService {

    UserProfileResponse getProfile(Long userId);
}
