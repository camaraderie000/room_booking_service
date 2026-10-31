package com.example.room_booking.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.room_booking.api.error.DomainException;
import com.example.room_booking.domain.Room;
import com.example.room_booking.repository.RoomRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room create(String name, int capacity, String description) {
        Room room = new Room(UUID.randomUUID(), name.trim(), capacity, description);
        try {
            roomRepository.insert(room);
        } catch (DuplicateKeyException e) {
            throw new DomainException(HttpStatus.CONFLICT, "ROOM_NAME_TAKEN",
                    "Комната с таким названием уже существует");
        }
        return room;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Room getById(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new DomainException(HttpStatus.NOT_FOUND,
                        "ROOM_NOT_FOUND", "Комната не найдена"));
    }
}