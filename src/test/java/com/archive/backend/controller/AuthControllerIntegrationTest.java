package com.archive.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void register_shouldReturn201_whenDataIsValid() throws Exception {
        String body = """
            {
                "username": "integrationuser",
                "email": "integration@example.com",
                "password": "password123"
            }
        """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void register_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        String firstBody = """
            {
                "username": "firstuser",
                "email": "duplicate@example.com",
                "password": "password123"
            }
        """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstBody))
            .andExpect(status().isCreated());

        String secondBody = """
            {
                "username": "seconduser",
                "email": "duplicate@example.com",
                "password": "password123"
            }
        """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(secondBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Email is already registered"));
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
        String registerBody = """
            {
                "username": "loginuser",
                "email": "login@example.com",
                "password": "password123"
            }
        """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody))
            .andExpect(status().isCreated());

        String loginBody = """
            {
                "email": "login@example.com",
                "password": "password123"
            }
        """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

}