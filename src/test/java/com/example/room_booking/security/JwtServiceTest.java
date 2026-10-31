package com.example.room_booking.security;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.example.room_booking.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    private static final String SECRET = "test-secret-test-secret-test-secret-123";

    private final JwtService service = new JwtService(SECRET, 60);
    private final User user = new User(UUID.randomUUID(), "a@example.com", "hash", "ADMIN");

    @Test
    void generatedTokenContainsUserIdAndRole() {
        String token = service.generateToken(user);

        Claims claims = service.parse(token);

        assertThat(claims.getSubject()).isEqualTo(user.id().toString());
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        JwtService other = new JwtService("another-secret-another-secret-12345678", 60);
        String foreignToken = other.generateToken(user);

        assertThatThrownBy(() -> service.parse(foreignToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void tamperedSignatureIsRejected() {
        String[] parts = service.generateToken(user).split("\\.");
        char first = parts[2].charAt(0);
        parts[2] = (first == 'A' ? 'B' : 'A') + parts[2].substring(1);
        String tampered = String.join(".", parts);

        assertThatThrownBy(() -> service.parse(tampered))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService shortLived = new JwtService(SECRET, -1);
        String expired = shortLived.generateToken(user);

        assertThatThrownBy(() -> service.parse(expired))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void garbageIsRejected() {
        assertThatThrownBy(() -> service.parse("abc.def.ghi"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void tooShortSecretFailsOnCreation() {
        assertThatThrownBy(() -> new JwtService("short", 60))
                .isInstanceOf(RuntimeException.class);
    }
}