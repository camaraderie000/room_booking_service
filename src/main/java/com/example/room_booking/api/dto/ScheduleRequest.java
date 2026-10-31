package com.example.room_booking.api.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ScheduleRequest(
        @NotEmpty List<DayOfWeek> daysOfWeek,
        @NotNull LocalTime openTime,
        @NotNull LocalTime closeTime,
        @Min(5) @Max(480) int slotMinutes,
        String timezone) {}