package com.promptcraft.promptcraft.mapper;

import com.promptcraft.promptcraft.dto.auth.SignupRequest;
import com.promptcraft.promptcraft.dto.auth.UserProfileResponse;
import com.promptcraft.promptcraft.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);

    UserProfileResponse toUserProfileResponse(User user);

}
