package com.example.room_booking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.example.room_booking.domain.Room;

@Repository
public class RoomRepository {

    private static final RowMapper<Room> MAPPER = (rs, rowNum) -> new Room(
            rs.getObject("id", UUID.class),
            rs.getString("name"),
            rs.getInt("capacity"),
            rs.getString("description"));

    private final JdbcClient jdbc;

    public RoomRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(Room room) {
        jdbc.sql("""
                INSERT INTO rooms (id, name, capacity, description)
                VALUES (:id, :name, :capacity, :description)
                """)
            .param("id", room.id())
            .param("name", room.name())
            .param("capacity", room.capacity())
            .param("description", room.description())
            .update();
    }

    public List<Room> findAll() {
        return jdbc.sql("SELECT id, name, capacity, description FROM rooms ORDER BY name")
            .query(MAPPER)
            .list();
    }

    public Optional<Room> findById(UUID id) {
        return jdbc.sql("SELECT id, name, capacity, description FROM rooms WHERE id = :id")
            .param("id", id)
            .query(MAPPER)
            .optional();
    }
}