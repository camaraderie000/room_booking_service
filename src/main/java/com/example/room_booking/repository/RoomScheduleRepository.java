package com.example.room_booking.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.example.room_booking.domain.RoomSchedule;

@Repository
public class RoomScheduleRepository {

    private final JdbcClient jdbc;

    public RoomScheduleRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void upsert(RoomSchedule s) {
        jdbc.sql("""
                INSERT INTO room_schedules (room_id, days_of_week, open_time, close_time, slot_minutes, timezone)
                VALUES (:roomId, :days, :openTime, :closeTime, :slotMinutes, :timezone)
                ON CONFLICT (room_id) DO UPDATE SET
                    days_of_week = EXCLUDED.days_of_week,
                    open_time    = EXCLUDED.open_time,
                    close_time   = EXCLUDED.close_time,
                    slot_minutes = EXCLUDED.slot_minutes,
                    timezone     = EXCLUDED.timezone
                """)
            .param("roomId", s.roomId())
            .param("days", daysToString(s.daysOfWeek()))
            .param("openTime", s.openTime())
            .param("closeTime", s.closeTime())
            .param("slotMinutes", s.slotMinutes())
            .param("timezone", s.timezone().getId())
            .update();
    }

    public Optional<RoomSchedule> findByRoomId(UUID roomId) {
        return jdbc.sql("""
                SELECT room_id, days_of_week, open_time, close_time, slot_minutes, timezone
                FROM room_schedules WHERE room_id = :roomId
                """)
            .param("roomId", roomId)
            .query((rs, rowNum) -> new RoomSchedule(
                    rs.getObject("room_id", UUID.class),
                    stringToDays(rs.getString("days_of_week")),
                    rs.getObject("open_time", LocalTime.class),
                    rs.getObject("close_time", LocalTime.class),
                    rs.getInt("slot_minutes"),
                    ZoneId.of(rs.getString("timezone"))))
            .optional();
    }

    private static String daysToString(Set<DayOfWeek> days) {
        return days.stream().sorted().map(Enum::name).collect(Collectors.joining(","));
    }

    private static Set<DayOfWeek> stringToDays(String value) {
        return Arrays.stream(value.split(","))
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class)));
    }
}