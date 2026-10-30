package com.example.room_booking.api.dto;

import java.util.UUID;

import com.example.room_booking.domain.User;

public record UserResponse(UUID id, String email, String role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.id(), user.email(), user.role());
    }
}