package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.auth.AuthResponse;
import com.promptcraft.promptcraft.dto.auth.LoginRequest;
import com.promptcraft.promptcraft.dto.auth.SignupRequest;
import com.promptcraft.promptcraft.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse signup(SignupRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
