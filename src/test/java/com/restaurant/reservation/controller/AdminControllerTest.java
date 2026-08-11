package com.restaurant.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.reservation.dto.TableInitializationRequest;
import com.restaurant.reservation.repository.TableRepository;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TableRepository tableRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void initializeTables_successful() throws Exception {
        TableInitializationRequest request = new TableInitializationRequest();
        request.setFixed(2);
        request.setLarge(3);
        request.setSmall(1);

        mockMvc.perform(post("/api/admin/tables/initialize")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tables configured successfully."));

        // Verify all tables were created with right number
        assertThat(tableRepository.count())
                .as("Total amount of created tables")
                .isEqualTo(6);
        assertThat(tableRepository.findByNumber("F2"))
                .as("Number of last fixed table created")
                .isPresent();
        assertThat(tableRepository.findByNumber("L5"))
                .as("Number of last large table created")
                .isPresent();
        assertThat(tableRepository.findByNumber("S6"))
                .as("Number of last small table created")
                .isPresent();
    }

    @Test
    void initializeTables_emptyRequestShouldInitializeWithZero() throws Exception {
        TableInitializationRequest request = new TableInitializationRequest();

        mockMvc.perform(post("/api/admin/tables/initialize")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tables configured successfully."));

        // Verify all tables were created
        assertThat(tableRepository.count())
                .as("Total amount of created tables")
                .isZero();
    }

    @Test
    void shouldReturnBadRequestWhenNegativeValue() throws Exception {
        String invalidJson = """
            {
                "fixed": -1
            }""";

        mockMvc.perform(post("/api/admin/tables/initialize")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("fixed: must be greater than or equal to 0"))
                .andExpect(jsonPath("$.correlationId").isNotEmpty());
    }

    @Test
    void shouldReplaceExistingTables() throws Exception {
        // First configuration: 2 fixed, 1 large
        TableInitializationRequest first = new TableInitializationRequest();
        first.setFixed(2);
        first.setLarge(1);

        mockMvc.perform(post("/api/admin/tables/initialize")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(first)));

        // Second configuration: 1 fixed, 2 small
        TableInitializationRequest second = new TableInitializationRequest();
        second.setFixed(1);
        second.setSmall(2);

        mockMvc.perform(post("/api/admin/tables/initialize")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(second)));

        // Verify only second configuration exists
        assertThat(tableRepository.count()).isEqualTo(3);
        assertThat(tableRepository.findByNumber("F1")).isPresent();
        assertThat(tableRepository.findByNumber("S2")).isPresent();
        assertThat(tableRepository.findByNumber("S3")).isPresent();
        assertThat(tableRepository.findByNumber("L1")).isNotPresent();
    }
}