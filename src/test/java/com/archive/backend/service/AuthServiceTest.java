package com.archive.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.archive.backend.repository.RoleRepository;
import com.archive.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock 
    private UserRepository userRepository;
    @Mock 
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach 
    void setUp() {
        authService = new AuthService(userRepository, roleRepository, passwordEncoder);
    }
    
}
