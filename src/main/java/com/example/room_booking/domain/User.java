package com.example.room_booking.domain;

import java.util.UUID;

public record User(UUID id, String email, String passwordHash, String role) {}