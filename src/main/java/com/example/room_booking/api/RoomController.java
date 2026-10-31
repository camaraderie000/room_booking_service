package com.example.room_booking.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.room_booking.api.dto.CreateRoomRequest;
import com.example.room_booking.api.dto.RoomResponse;
import com.example.room_booking.domain.Room;
import com.example.room_booking.security.AuthUser;
import com.example.room_booking.service.RoomService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(
            @RequestAttribute("currentUser") AuthUser currentUser,
            @Valid @RequestBody CreateRoomRequest request) {
        currentUser.requireAdmin();
        Room room = roomService.create(request.name(), request.capacity(), request.description());
        return ResponseEntity
                .created(URI.create("/api/v1/rooms/" + room.id()))
                .body(RoomResponse.from(room));
    }

    @GetMapping
    public List<RoomResponse> list() {
        return roomService.findAll().stream().map(RoomResponse::from).toList();
    }

    @GetMapping("/{id}")
    public RoomResponse get(@PathVariable UUID id) {
        return RoomResponse.from(roomService.getById(id));
    }
}