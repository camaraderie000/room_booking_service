package com.example.room_booking.domain;

import java.util.UUID;

public record Room(UUID id, String name, int capacity, String description) {}