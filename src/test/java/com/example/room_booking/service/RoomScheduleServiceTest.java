package com.example.room_booking.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;

import com.example.room_booking.api.error.DomainException;
import com.example.room_booking.domain.RoomSchedule;
import com.example.room_booking.repository.RoomScheduleRepository;

class RoomScheduleServiceTest {

    private final RoomScheduleRepository repository = mock(RoomScheduleRepository.class);
    private final RoomService roomService = mock(RoomService.class);
    private final RoomScheduleService service = new RoomScheduleService(repository, roomService);

    private final UUID roomId = UUID.randomUUID();
    private final LocalTime nine = LocalTime.of(9, 0);
    private final LocalTime eighteen = LocalTime.of(18, 0);

    @Test
    void validScheduleIsSaved() {
        service.save(roomId, List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), nine, eighteen, 30, "Europe/London");

        ArgumentCaptor<RoomSchedule> saved = ArgumentCaptor.forClass(RoomSchedule.class);
        verify(repository).upsert(saved.capture());
        RoomSchedule s = saved.getValue();

        assertThat(s.roomId()).isEqualTo(roomId);
        assertThat(s.daysOfWeek()).containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        assertThat(s.slotMinutes()).isEqualTo(30);
        assertThat(s.timezone().getId()).isEqualTo("Europe/London");
    }

    @Test
    void missingTimezoneDefaultsToUtc() {
        service.save(roomId, List.of(DayOfWeek.MONDAY), nine, eighteen, 30, null);

        ArgumentCaptor<RoomSchedule> saved = ArgumentCaptor.forClass(RoomSchedule.class);
        verify(repository).upsert(saved.capture());
        assertThat(saved.getValue().timezone().getId()).isEqualTo("UTC");
    }

    @Test
    void closeNotAfterOpenIsRejected() {
        assertThatThrownBy(() -> service.save(roomId, List.of(DayOfWeek.MONDAY), eighteen, nine, 30, "UTC"))
                .satisfies(this::assertInvalidSchedule);
        verify(repository, never()).upsert(any());
    }

    @Test
    void slotThatDoesNotDivideTheDayIsRejected() {
        // 9 hours = 540 min, 540 % 40 != 0
        assertThatThrownBy(() -> service.save(roomId, List.of(DayOfWeek.MONDAY), nine, eighteen, 40, "UTC"))
                .satisfies(this::assertInvalidSchedule);
    }

    @Test
    void emptyDaysAreRejected() {
        assertThatThrownBy(() -> service.save(roomId, List.of(), nine, eighteen, 30, "UTC"))
                .satisfies(this::assertInvalidSchedule);
    }

    @Test
    void unknownTimezoneIsRejected() {
        assertThatThrownBy(() -> service.save(roomId, List.of(DayOfWeek.MONDAY), nine, eighteen, 30, "Mars/Base"))
                .satisfies(this::assertInvalidSchedule);
    }

    @Test
    void unknownRoomGives404AndNothingIsSaved() {
        when(roomService.getById(roomId))
                .thenThrow(new DomainException(HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND", "Комната не найдена"));

        assertThatThrownBy(() -> service.save(roomId, List.of(DayOfWeek.MONDAY), nine, eighteen, 30, "UTC"))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("ROOM_NOT_FOUND"));
        verify(repository, never()).upsert(any());
    }

    private void assertInvalidSchedule(Throwable t) {
        assertThat(t).isInstanceOfSatisfying(DomainException.class, e -> {
            assertThat(e.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(e.getCode()).isEqualTo("INVALID_SCHEDULE");
        });
    }
}