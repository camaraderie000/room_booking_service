package com.example.room_booking.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.room_booking.api.error.DomainException;
import com.example.room_booking.domain.RoomSchedule;
import com.example.room_booking.domain.Slot;
import com.example.room_booking.domain.SlotGenerator;
import com.example.room_booking.domain.TimeSlot;
import com.example.room_booking.repository.SlotRepository;

@Service
public class SlotService {

    static final int MAX_GENERATE_DAYS = 60;
    static final Duration MAX_WINDOW = Duration.ofDays(31);

    private final SlotRepository slotRepository;
    private final RoomScheduleService scheduleService;
    private final RoomService roomService;

    public SlotService(SlotRepository slotRepository, RoomScheduleService scheduleService,
                       RoomService roomService) {
        this.slotRepository = slotRepository;
        this.scheduleService = scheduleService;
        this.roomService = roomService;
    }

    @Transactional
    public int generate(UUID roomId, int days) {
        if (days < 1 || days > MAX_GENERATE_DAYS) {
            throw invalidRange("Количество дней должно быть от 1 до " + MAX_GENERATE_DAYS);
        }
        RoomSchedule schedule = scheduleService.get(roomId);
        LocalDate today = LocalDate.now(schedule.timezone());
        List<TimeSlot> slots = SlotGenerator.generate(schedule, today, today.plusDays(days));
        return slotRepository.insertAll(roomId, slots);
    }

    public List<Slot> list(UUID roomId, Instant from, Instant to) {
        roomService.getById(roomId);
        if (!from.isBefore(to)) {
            throw invalidRange("Параметр from должен быть раньше to");
        }
        if (Duration.between(from, to).compareTo(MAX_WINDOW) > 0) {
            throw invalidRange("Максимальный период запроса: 31 день");
        }
        return slotRepository.findInRange(roomId, from, to);
    }

    private DomainException invalidRange(String message) {
        return new DomainException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_RANGE", message);
    }
}