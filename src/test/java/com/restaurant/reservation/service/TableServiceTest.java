package com.restaurant.reservation.service;

import com.restaurant.reservation.domain.enums.TableType;
import com.restaurant.reservation.domain.models.RestaurantTable;
import com.restaurant.reservation.dto.TableInitializationRequest;
import com.restaurant.reservation.exception.BusinessException;
import com.restaurant.reservation.repository.TableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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

    @Test
    void initializeTables_shouldNotSaveDataWhenRequestIsEmpty() {
        TableInitializationRequest request = new TableInitializationRequest();

        tableService.initializeTables(request);

        // Verify previous data is deleted and no new data is saved
        verify(tableRepository).deleteAllInBatch();
        verifyNoMoreInteractions(tableRepository);
    }

    @Test
    void initializeTables_shouldNotSaveDataWhenAllCountsAreZero() {
        TableInitializationRequest request = new TableInitializationRequest();
        request.setFixed(0);
        request.setLarge(0);
        request.setSmall(0);

        tableService.initializeTables(request);

        // Verify previous data is deleted and no new data is saved
        verify(tableRepository).deleteAllInBatch();
        verifyNoMoreInteractions(tableRepository);
    }

    @Test
    void initializeTables_shouldThrowExceptionWhenNegativeValues() {
        TableInitializationRequest request = new TableInitializationRequest();
        request.setFixed(-1);

        assertThatThrownBy(() -> tableService.initializeTables(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Table quantities cannot be negative.");

        verify(tableRepository, never()).deleteAllInBatch();
        verify(tableRepository, never()).saveAll(any());
    }

    @Test
    void getAllTables_shouldCountTablesByType() {
        // Preparar datos mock
        RestaurantTable fixed1 = RestaurantTable.builder()
                .number("F1").type(TableType.FIXED).build();
        RestaurantTable fixed2 = RestaurantTable.builder()
                .number("F2").type(TableType.FIXED).build();
        RestaurantTable large1 = RestaurantTable.builder()
                .number("L1").type(TableType.LARGE).build();
        RestaurantTable small1 = RestaurantTable.builder()
                .number("S1").type(TableType.SMALL).build();

        when(tableRepository.findAll()).thenReturn(List.of(fixed1, fixed2, large1, small1));

        Map<TableType, Integer> result = tableService.getAllTables();

        assertThat(result.get(TableType.FIXED)).isEqualTo(2);
        assertThat(result.get(TableType.LARGE)).isEqualTo(1);
        assertThat(result.get(TableType.SMALL)).isEqualTo(1);
    }

    @Test
    void getAllTables_shouldReturnZeroCountsWhenNoTablesExist() {
        when(tableRepository.findAll()).thenReturn(Collections.emptyList());

        Map<TableType, Integer> result = tableService.getAllTables();

        assertThat(result.get(TableType.FIXED)).isZero();
        assertThat(result.get(TableType.LARGE)).isZero();
        assertThat(result.get(TableType.SMALL)).isZero();
    }
}