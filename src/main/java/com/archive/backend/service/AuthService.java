package com.archive.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.archive.backend.dto.AuthResponse;
import com.archive.backend.dto.RegisterRequest;
import com.archive.backend.entity.Role;
import com.archive.backend.entity.User;
import com.archive.backend.repository.RoleRepository;
import com.archive.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email is already registered");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username is already taken");
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

        return new AuthResponse("User registered successfully");

    }

}
