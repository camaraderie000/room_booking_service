package com.example.room_booking.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.room_booking.api.error.DomainException;
import com.example.room_booking.domain.User;
import com.example.room_booking.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DomainException(HttpStatus.UNAUTHORIZED,
                        "UNAUTHORIZED", "Пользователь не найден"));
    }
}