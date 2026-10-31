package com.example.room_booking.service;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.room_booking.api.error.DomainException;
import com.example.room_booking.domain.User;
import com.example.room_booking.repository.UserRepository;
import com.example.room_booking.security.JwtService;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerNormalizesEmailAndHashesPassword() {
        service.register("  Test@Example.COM ", "password123");

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).insert(saved.capture());
        User user = saved.getValue();

        assertThat(user.email()).isEqualTo("test@example.com");
        assertThat(user.role()).isEqualTo("USER");
        assertThat(user.passwordHash()).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", user.passwordHash())).isTrue();
    }

    @Test
    void registerDuplicateEmailGives409() {
        doThrow(new DuplicateKeyException("dup")).when(userRepository).insert(any());

        assertThatThrownBy(() -> service.register("a@example.com", "password123"))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(e.getCode()).isEqualTo("EMAIL_TAKEN");
                });
    }

    @Test
    void loginReturnsTokenForCorrectPassword() {
        User user = new User(UUID.randomUUID(), "a@example.com",
                passwordEncoder.encode("password123"), "USER");
        when(userRepository.findByEmail("a@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("the-token");

        assertThat(service.login("A@example.com", "password123")).isEqualTo("the-token");
    }

    @Test
    void loginWithWrongPasswordGives401() {
        User user = new User(UUID.randomUUID(), "a@example.com",
                passwordEncoder.encode("password123"), "USER");
        when(userRepository.findByEmail("a@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.login("a@example.com", "wrong-password"))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    assertThat(e.getCode()).isEqualTo("INVALID_CREDENTIALS");
                });
    }

    @Test
    void loginWithUnknownEmailGivesSameErrorAsWrongPassword() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login("nobody@example.com", "password123"))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    assertThat(e.getCode()).isEqualTo("INVALID_CREDENTIALS");
                });
    }
}