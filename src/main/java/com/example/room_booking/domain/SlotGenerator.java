package com.example.room_booking.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public final class SlotGenerator {

    private SlotGenerator() {}

    public static List<TimeSlot> generate(RoomSchedule schedule, LocalDate fromInclusive, LocalDate toExclusive) {
        List<TimeSlot> result = new ArrayList<>();
        ZoneId zone = schedule.timezone();
        int step = schedule.slotMinutes() * 60;
        int closeSec = schedule.closeTime().toSecondOfDay();

        for (LocalDate date = fromInclusive; date.isBefore(toExclusive); date = date.plusDays(1)) {
            if (!schedule.daysOfWeek().contains(date.getDayOfWeek())) {
                continue;
            }
            int cursor = schedule.openTime().toSecondOfDay();
            while (cursor + step <= closeSec) {
                result.add(new TimeSlot(
                        toInstant(date, cursor, zone),
                        toInstant(date, cursor + step, zone)));
                cursor += step;
            }
        }
        return result;
    }

    private static java.time.Instant toInstant(LocalDate date, int secondOfDay, ZoneId zone) {
        return ZonedDateTime.of(date, LocalTime.ofSecondOfDay(secondOfDay), zone).toInstant();
    }
}