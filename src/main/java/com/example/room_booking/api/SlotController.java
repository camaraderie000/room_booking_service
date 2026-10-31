package com.example.room_booking.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.room_booking.api.dto.GenerateSlotsResponse;
import com.example.room_booking.api.dto.SlotResponse;
import com.example.room_booking.security.AuthUser;
import com.example.room_booking.service.SlotService;

@RestController
@RequestMapping("/api/v1/rooms/{roomId}/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping
    public List<SlotResponse> list(@PathVariable UUID roomId,
                                   @RequestParam Instant from,
                                   @RequestParam Instant to) {
        return slotService.list(roomId, from, to).stream().map(SlotResponse::from).toList();
    }

    @PostMapping("/generate")
    public GenerateSlotsResponse generate(@RequestAttribute("currentUser") AuthUser currentUser,
                                          @PathVariable UUID roomId,
                                          @RequestParam(defaultValue = "14") int days) {
        currentUser.requireAdmin();
        return new GenerateSlotsResponse(slotService.generate(roomId, days));
    }
}