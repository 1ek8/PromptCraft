package com.promptcraft.promptcraft.dto.auth;

public record AuthResponse(
        String token,
        UserProfileResponse user) {
    //the fields are private + final by default for record
    //get constructor for all fields and getter methods too



}
