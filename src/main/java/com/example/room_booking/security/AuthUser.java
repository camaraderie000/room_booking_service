package com.example.room_booking.security;

import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.example.room_booking.api.error.DomainException;

public record AuthUser(UUID id, String role) {

    public void requireAdmin() {
        if (!"ADMIN".equals(role)) {
            throw new DomainException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Недостаточно прав");
        }
    }
}