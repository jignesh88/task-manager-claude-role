package com.grabduck.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grabduck.taskmanager.dto.AuthenticationRequestDto;
import com.grabduck.taskmanager.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() throws Exception {
        // Create a test user first
        UserDto userDto = new UserDto();
        userDto.setUsername(TEST_USERNAME);
        userDto.setPassword(TEST_PASSWORD);
        userDto.setEmail("test@example.com");

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    void loginSuccess() throws Exception {
        // Given
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        // When
        ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void loginFailure_InvalidCredentials() throws Exception {
        // Given
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setUsername(TEST_USERNAME);
        request.setPassword("wrongpassword");

        // When
        ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void loginFailure_InvalidRequest() throws Exception {
        // Given
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        // Missing username and password

        // When
        ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isBadRequest());
    }
}
