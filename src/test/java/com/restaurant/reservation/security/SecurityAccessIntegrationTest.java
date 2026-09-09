package com.restaurant.reservation.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.reservation.dto.JwtResponse;
import com.restaurant.reservation.dto.LogInRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String obtainToken(String email, String password) throws Exception {
        LogInRequest login = new LogInRequest();
        login.setEmail(email);
        login.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                JwtResponse.class
        );
        return response.getToken();
    }

    @Test
    void adminEndpoint_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin/tables"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_withUserToken_shouldReturn403() throws Exception {
        String token = obtainToken("user@user.com", "user");

        mockMvc.perform(get("/api/admin/tables")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_withAdminToken_shouldReturn200() throws Exception {
        String token = obtainToken("admin@admin.com", "admin");

        mockMvc.perform(get("/api/admin/tables")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
