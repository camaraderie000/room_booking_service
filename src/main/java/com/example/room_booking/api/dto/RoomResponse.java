package com.example.room_booking.api.dto;

import java.util.UUID;

import com.example.room_booking.domain.Room;

public record RoomResponse(UUID id, String name, int capacity, String description) {

    public static RoomResponse from(Room room) {
        return new RoomResponse(room.id(), room.name(), room.capacity(), room.description());
    }
}