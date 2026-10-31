package com.example.room_booking.service;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.EnumSet;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.room_booking.api.error.DomainException;
import com.example.room_booking.domain.RoomSchedule;
import com.example.room_booking.repository.RoomScheduleRepository;

@Service
public class RoomScheduleService {

    private final RoomScheduleRepository repository;
    private final RoomService roomService;

    public RoomScheduleService(RoomScheduleRepository repository, RoomService roomService) {
        this.repository = repository;
        this.roomService = roomService;
    }

    public RoomSchedule save(UUID roomId, Collection<DayOfWeek> days, LocalTime open,
                             LocalTime close, int slotMinutes, String timezone) {
        roomService.getById(roomId); // 404, если комнаты нет

        if (days == null || days.isEmpty()) {
            throw invalid("Нужно указать хотя бы один день недели");
        }
        if (!open.isBefore(close)) {
            throw invalid("Время открытия должно быть раньше времени закрытия");
        }
        if (Duration.between(open, close).toMinutes() % slotMinutes != 0) {
            throw invalid("Рабочий день должен делиться на слоты без остатка");
        }

        ZoneId zone;
        try {
            zone = ZoneId.of(timezone == null || timezone.isBlank() ? "UTC" : timezone);
        } catch (DateTimeException e) {
            throw invalid("Неизвестный часовой пояс");
        }

        RoomSchedule schedule = new RoomSchedule(roomId, EnumSet.copyOf(days), open, close, slotMinutes, zone);
        repository.upsert(schedule);
        return schedule;
    }

    public RoomSchedule get(UUID roomId) {
        roomService.getById(roomId);
        return repository.findByRoomId(roomId)
                .orElseThrow(() -> new DomainException(HttpStatus.NOT_FOUND,
                        "SCHEDULE_NOT_FOUND", "У комнаты ещё нет расписания"));
    }

    private DomainException invalid(String message) {
        return new DomainException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_SCHEDULE", message);
    }
}