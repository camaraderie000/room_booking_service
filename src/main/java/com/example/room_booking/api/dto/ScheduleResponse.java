package com.example.room_booking.api.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.example.room_booking.domain.RoomSchedule;

public record ScheduleResponse(
        UUID roomId,
        List<DayOfWeek> daysOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        int slotMinutes,
        String timezone) {

    public static ScheduleResponse from(RoomSchedule s) {
        return new ScheduleResponse(s.roomId(), s.daysOfWeek().stream().sorted().toList(),
                s.openTime(), s.closeTime(), s.slotMinutes(), s.timezone().getId());
    }
}