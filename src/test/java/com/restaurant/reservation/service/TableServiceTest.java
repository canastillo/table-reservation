package com.restaurant.reservation.service;

import com.restaurant.reservation.domain.models.RestaurantTable;
import com.restaurant.reservation.dto.TableInitializationRequest;
import com.restaurant.reservation.repository.TableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @Mock
    private TableRepository tableRepository;

    @InjectMocks
    private TableService tableService;

    @Test
    void initializeTables_shouldDeleteExistingTablesAndInsertNewOnes() {
        TableInitializationRequest request = new TableInitializationRequest();
        request.setFixed(1);
        request.setLarge(1);
        request.setSmall(1);

        tableService.initializeTables(request);

        // Verify invoke to deleteAllInBatch()
        verify(tableRepository).deleteAllInBatch();

        ArgumentCaptor<List<RestaurantTable>> captor = ArgumentCaptor.forClass(List.class);
        verify(tableRepository).saveAll(captor.capture());

        List<RestaurantTable> savedTables = captor.getValue();

        // Verify right amount and number of created tables
        assertThat(savedTables).as("Amount of created tables").hasSize(3);
        assertThat(savedTables.stream().map(RestaurantTable::getNumber))
                .as("Generated table numbers")
                .containsExactlyInAnyOrder("F1", "L2", "S3");
    }
}