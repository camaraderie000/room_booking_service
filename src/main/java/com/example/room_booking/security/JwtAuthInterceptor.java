package com.example.room_booking.security;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.room_booking.api.error.DomainException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    public static final String CURRENT_USER = "currentUser";

    private final JwtService jwtService;

    public JwtAuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw unauthorized("Требуется авторизация");
        }
        String token = header.substring("Bearer ".length());

        AuthUser user;
        try {
            Claims claims = jwtService.parse(token);
            user = new AuthUser(UUID.fromString(claims.getSubject()), claims.get("role", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            throw unauthorized("Недействительный или просроченный токен");
        }
        request.setAttribute(CURRENT_USER, user);
        return true;
    }

    private DomainException unauthorized(String message) {
        return new DomainException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }
}