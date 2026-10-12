package com.archive.backend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.archive.backend.entity.Role;
import com.archive.backend.entity.User;
import com.archive.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    
    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        Role role = Role.builder().name("USER").build();
        User user = User.builder()
            .username("testuser")
            .password("encoded")
            .roles(Set.of(role))
            .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("testuser");

        assertThat(details.getUsername()).isEqualTo("testuser");
        assertThat(details.getPassword()).isEqualTo("encoded");
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
            .isInstanceOf(UsernameNotFoundException.class);
    }

}