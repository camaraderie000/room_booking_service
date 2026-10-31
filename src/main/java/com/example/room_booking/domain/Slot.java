package com.example.room_booking.domain;

import java.time.Instant;
import java.util.UUID;

public record Slot(UUID id, UUID roomId, Instant startAt, Instant endAt) {}