package com.example.room_booking.security;

import java.util.UUID;

public record AuthUser(UUID id, String role) {}