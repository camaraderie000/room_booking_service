package com.example.room_booking.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

public record RoomSchedule(
        UUID roomId,
        Set<DayOfWeek> daysOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        int slotMinutes,
        ZoneId timezone) {}