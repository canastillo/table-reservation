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

    //@Autowired
    //private ObjectMapper objectMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TableRepository tableRepository;

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
                .andExpect(jsonPath("$.message").value("Mesas configuradas exitosamente"));

        // Verify all tables were created
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
}