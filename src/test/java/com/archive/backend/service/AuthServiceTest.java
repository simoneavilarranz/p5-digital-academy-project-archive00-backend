package com.archive.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.archive.backend.entity.User;
import com.archive.backend.exception.EmailAlreadyExistsException;
import com.archive.backend.exception.UsernameAlreadyExistsException;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.archive.backend.dto.AuthResponse;
import com.archive.backend.dto.LoginRequest;
import com.archive.backend.dto.RegisterRequest;
import com.archive.backend.entity.Role;
import com.archive.backend.repository.RoleRepository;
import com.archive.backend.repository.UserRepository;
import com.archive.backend.security.JwtService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock 
    private UserRepository userRepository;
    @Mock 
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach 
    void setUp() {
        authService = new AuthService(userRepository, roleRepository, passwordEncoder, jwtService);
    }

    @Test 
    void register_shouldReturnSuccessMessage_whenDataIsValid() {

        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");
        Role userRole = Role.builder().name("USER").build();

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        AuthResponse response = authService.register(request);

        assertThat(response.message()).isEqualTo("User registered successfully");
        verify(userRepository).save(any(User.class));

    }

    @Test 
    void register_shouldThrowEmailAlreadyExistsException_whenEmailExists() {

        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(EmailAlreadyExistsException.class)
        .hasMessage("Email is already registered");

    }

    @Test 
    void register_shouldThrowUsernameAlreadyExistsException_whenUsernameExists() {

        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");
        
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(UsernameAlreadyExistsException.class)
        .hasMessage("Username is already taken");

    }
    
    @Test 
    void login_shouldReturnToken_whenCredentialsAreValid() {

        LoginRequest request = new LoginRequest("test@example.com", "password123");

        User user = User.builder()
            .username("testuser")
            .email("test@example.com")
            .password("encodedPassword")
            .build();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getUsername())).thenReturn("mockedToken");

        AuthResponse response = authService.login(request);

        assertThat(response.message()).isEqualTo("Login successful");
        assertThat(response.token()).isEqualTo("mockedToken");

    }

}
