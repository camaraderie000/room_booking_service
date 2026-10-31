package com.example.room_booking.repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.example.room_booking.domain.Slot;
import com.example.room_booking.domain.TimeSlot;

@Repository
public class SlotRepository {

    private final JdbcClient jdbc;
    private final JdbcTemplate jdbcTemplate;

    public SlotRepository(JdbcClient jdbc, JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbc;
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertAll(UUID roomId, List<TimeSlot> slots) {
        if (slots.isEmpty()) {
            return 0;
        }
        List<Object[]> batch = slots.stream()
                .map(s -> new Object[] {UUID.randomUUID(), roomId, utc(s.startAt()), utc(s.endAt())})
                .toList();
        int[] results = jdbcTemplate.batchUpdate("""
                INSERT INTO slots (id, room_id, start_at, end_at)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (room_id, start_at) DO NOTHING
                """, batch);
        return Arrays.stream(results).filter(r -> r > 0).sum();
    }

    public List<Slot> findInRange(UUID roomId, Instant from, Instant to) {
        return jdbc.sql("""
                SELECT id, room_id, start_at, end_at FROM slots
                WHERE room_id = :roomId AND start_at >= :from AND start_at < :to
                ORDER BY start_at
                """)
            .param("roomId", roomId)
            .param("from", utc(from))
            .param("to", utc(to))
            .query((rs, rowNum) -> new Slot(
                    rs.getObject("id", UUID.class),
                    rs.getObject("room_id", UUID.class),
                    rs.getObject("start_at", OffsetDateTime.class).toInstant(),
                    rs.getObject("end_at", OffsetDateTime.class).toInstant()))
            .list();
    }

    private static OffsetDateTime utc(Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}