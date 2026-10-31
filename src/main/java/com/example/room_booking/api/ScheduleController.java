package com.example.room_booking.api;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.room_booking.api.dto.ScheduleRequest;
import com.example.room_booking.api.dto.ScheduleResponse;
import com.example.room_booking.security.AuthUser;
import com.example.room_booking.service.RoomScheduleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rooms/{roomId}/schedule")
public class ScheduleController {

    private final RoomScheduleService scheduleService;

    public ScheduleController(RoomScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PutMapping
    public ScheduleResponse put(@RequestAttribute("currentUser") AuthUser currentUser,
                                @PathVariable UUID roomId,
                                @Valid @RequestBody ScheduleRequest request) {
        currentUser.requireAdmin();
        return ScheduleResponse.from(scheduleService.save(roomId, request.daysOfWeek(),
                request.openTime(), request.closeTime(), request.slotMinutes(), request.timezone()));
    }

    @GetMapping
    public ScheduleResponse get(@PathVariable UUID roomId) {
        return ScheduleResponse.from(scheduleService.get(roomId));
    }
}