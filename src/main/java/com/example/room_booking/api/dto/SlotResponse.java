package com.example.room_booking.api.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.room_booking.domain.Slot;

public record SlotResponse(UUID id, Instant startAt, Instant endAt) {

    public static SlotResponse from(Slot slot) {
        return new SlotResponse(slot.id(), slot.startAt(), slot.endAt());
    }
}