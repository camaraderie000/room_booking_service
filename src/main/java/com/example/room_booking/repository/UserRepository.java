package com.example.room_booking.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.example.room_booking.domain.User;

@Repository
public class UserRepository {

    private final JdbcClient jdbc;

    public UserRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(User user) {
        jdbc.sql("""
                INSERT INTO users (id, email, password_hash, role)
                VALUES (:id, :email, :passwordHash, :role)
                """)
            .param("id", user.id())
            .param("email", user.email())
            .param("passwordHash", user.passwordHash())
            .param("role", user.role())
            .update();
    }

    public Optional<User> findByEmail(String email) {
        return jdbc.sql("SELECT id, email, password_hash, role FROM users WHERE email = :email")
            .param("email", email)
            .query((rs, rowNum) -> new User(
                rs.getObject("id", UUID.class),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role")))
            .optional();
    }
}