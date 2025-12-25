package com.promptcraft.promptcraft.controller;

import com.promptcraft.promptcraft.dto.auth.AuthResponse;
import com.promptcraft.promptcraft.dto.auth.SignupRequest;
import com.promptcraft.promptcraft.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public ResponseEntity<AuthResponse> signup(SignupRequest){
        return null;
    }

}
