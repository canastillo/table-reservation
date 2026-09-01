package com.restaurant.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.reservation.dto.LogInRequest;
import com.restaurant.reservation.dto.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerUser_shouldCreateUserAndReturnOk() throws Exception {
        SignUpRequest signup = new SignUpRequest();
        signup.setFullName("New User");
        signup.setEmail("newuser@example.com");
        signup.setPassword("password123");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenEmailExists() throws Exception {
        SignUpRequest signup = new SignUpRequest();
        signup.setFullName("User");
        signup.setEmail("user@user.com"); // already seeded
        signup.setPassword("password123");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use"));
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenInvalidData() throws Exception {
        SignUpRequest signup = new SignUpRequest();
        signup.setFullName("");
        signup.setEmail("invalid-email");
        signup.setPassword("123");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.correlationId").isNotEmpty());
    }

    @Test
    void authenticateUser_shouldReturnJwtWithUserRole() throws Exception {
        LogInRequest login = new LogInRequest();
        login.setEmail("user@user.com");
        login.setPassword("user");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    void authenticateUser_shouldReturnJwtWithAdminRole() throws Exception {
        LogInRequest login = new LogInRequest();
        login.setEmail("admin@admin.com");
        login.setPassword("admin");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    void authenticateUser_shouldReturnUnauthorizedForBadCredentials() throws Exception {
        LogInRequest login = new LogInRequest();
        login.setEmail("user@user.com");
        login.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.correlationId").isNotEmpty());
    }
}