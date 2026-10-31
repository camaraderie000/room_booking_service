package com.example.room_booking.service;

import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.room_booking.api.error.DomainException;
import com.example.room_booking.domain.User;
import com.example.room_booking.repository.UserRepository;
import com.example.room_booking.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthService(UserRepository userRepository,
                    PasswordEncoder passwordEncoder,
                    JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    public String login(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .filter(u -> passwordEncoder.matches(password, u.passwordHash()))
                .orElseThrow(() -> new DomainException(HttpStatus.UNAUTHORIZED,
                        "INVALID_CREDENTIALS", "Неверный email или пароль"));
        return jwtService.generateToken(user);
    }

    
}