package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.advice.exceptions.BadRequestException;
import com.promptcraft.promptcraft.dto.auth.AuthResponse;
import com.promptcraft.promptcraft.dto.auth.LoginRequest;
import com.promptcraft.promptcraft.dto.auth.SignupRequest;
import com.promptcraft.promptcraft.entity.User;
import com.promptcraft.promptcraft.mapper.UserMapper;
import com.promptcraft.promptcraft.repository.UserRepository;
import com.promptcraft.promptcraft.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse signup(SignupRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(
                user -> {
                    throw new BadRequestException("A user with the username" + request.username() +" already exists");
                }
        );

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return new AuthResponse("placeholder", userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {

//        User user = userRepository.findByUsername(request.username()).orElseThrow(
//                 throw new BadRequestException("No user with username " + request.username() + "exists in the database");
//        );

        return null;
    }
}
