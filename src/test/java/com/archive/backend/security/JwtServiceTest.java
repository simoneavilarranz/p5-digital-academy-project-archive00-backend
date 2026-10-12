package com.archive.backend.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secret", 
            "test_secret_key_for_jwt_tokens_must_be_long_enough_for_hs512_algorithm_1234567890");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hora
    }

    @Test
    void generateToken_shouldReturnNonEmptyToken() {
        String token = jwtService.generateToken("testuser");

        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken("testuser");

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenTokenIsValid() {
        String token = jwtService.generateToken("testuser");

        boolean isValid = jwtService.isTokenValid(token, "testuser");

        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenUsernameDoesNotMatch() {
        String token = jwtService.generateToken("testuser");

        boolean isValid = jwtService.isTokenValid(token, "otheruser");

        assertThat(isValid).isFalse();
    }
}