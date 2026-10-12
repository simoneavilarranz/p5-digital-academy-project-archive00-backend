package com.archive.backend.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEmailAlreadyExists_shouldReturn409() {
        ResponseEntity<Map<String, Object>> response =
            handler.handleEmailAlreadyExists(new EmailAlreadyExistsException("Email taken"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("status", 409);
        assertThat(response.getBody()).containsEntry("message", "Email taken");
    }

    @Test
    void handleUsernameAlreadyExists_shouldReturn409() {
        ResponseEntity<Map<String, Object>> response =
            handler.handleUsernameAlreadyExists(new UsernameAlreadyExistsException("Username taken"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("status", 409);
    }

    @Test
    void handleInvalidCredentials_shouldReturn401() {
        ResponseEntity<Map<String, Object>> response =
            handler.handleInvalidCredentials(new InvalidCredentialsException("Invalid"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("status", 401);
    }

}