package com.example.room_booking.service;

import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.room_booking.api.error.DomainException;
import com.example.room_booking.domain.User;
import com.example.room_booking.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        User user = new User(
                UUID.randomUUID(),
                normalizedEmail,
                passwordEncoder.encode(password),
                "USER");
        try {
            userRepository.insert(user);
        } catch (DuplicateKeyException e) {
            throw new DomainException(HttpStatus.CONFLICT, "EMAIL_TAKEN",
                    "Пользователь с таким email уже существует");
        }
        return user;
    }
}