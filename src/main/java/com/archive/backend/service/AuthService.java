package com.archive.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.archive.backend.dto.AuthResponse;
import com.archive.backend.dto.LoginRequest;
import com.archive.backend.dto.RegisterRequest;
import com.archive.backend.entity.Role;
import com.archive.backend.entity.User;
import com.archive.backend.exception.EmailAlreadyExistsException;
import com.archive.backend.exception.InvalidCredentialsException;
import com.archive.backend.exception.UsernameAlreadyExistsException;
import com.archive.backend.repository.RoleRepository;
import com.archive.backend.repository.UserRepository;
import com.archive.backend.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }

        Role userRole = roleRepository.findByName("USER")
        .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = User.builder()
        .username(request.username())
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .build();
        user.getRoles().add(userRole);

        userRepository.save(user);

        return new AuthResponse("User registered successfully", null);

    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponse("Login succesful", token);

    }

}
