package com.example.room_booking.domain;

import java.time.Instant;

public record TimeSlot(Instant startAt, Instant endAt) {}