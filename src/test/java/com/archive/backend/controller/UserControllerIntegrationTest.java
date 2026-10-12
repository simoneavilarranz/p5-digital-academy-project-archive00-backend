package com.archive.backend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.archive.backend.entity.User;
import com.archive.backend.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "testuser")
    void getMe_shouldReturnUserResponse() throws Exception {
        User user = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .createdAt(LocalDateTime.now())
            .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }

}