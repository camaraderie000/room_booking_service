package com.example.room_booking.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.room_booking.api.dto.UserResponse;
import com.example.room_booking.security.AuthUser;
import com.example.room_booking.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse me(@RequestAttribute("currentUser") AuthUser currentUser) {
        return UserResponse.from(userService.getById(currentUser.id()));
    }
}