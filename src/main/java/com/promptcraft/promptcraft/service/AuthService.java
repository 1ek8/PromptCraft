package com.promptcraft.promptcraft.service;

import com.promptcraft.promptcraft.dto.auth.AuthResponse;
import com.promptcraft.promptcraft.dto.auth.LoginRequest;
import com.promptcraft.promptcraft.dto.auth.SignupRequest;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
