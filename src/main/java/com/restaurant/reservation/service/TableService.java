package com.restaurant.reservation.service;

import com.restaurant.reservation.domain.enums.TableType;
import com.restaurant.reservation.domain.models.RestaurantTable;
import com.restaurant.reservation.dto.TableInitializationRequest;
import com.restaurant.reservation.exception.BusinessException;
import com.restaurant.reservation.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;

    @Transactional
    public void initializeTables(TableInitializationRequest request) {
        if (request.getFixed() < 0 || request.getLarge() < 0 || request.getSmall() < 0) {
            throw new BusinessException("Table quantities cannot be negative.");
        }

        // TODO: Think of the clean up strategy
        tableRepository.deleteAllInBatch();

        List<RestaurantTable> newTables = new ArrayList<>();
        int counter = 1;

        for (int i = 0; i < request.getFixed(); i++) {
            newTables.add(RestaurantTable.builder()
                    .number("F" + counter++)
                    .type(TableType.FIXED)
                    .build());
        }

        for (int i = 0; i < request.getLarge(); i++) {
            newTables.add(RestaurantTable.builder()
                    .number("L" + counter++)
                    .type(TableType.LARGE)
                    .build());
        }

        for (int i = 0; i < request.getSmall(); i++) {
            newTables.add(RestaurantTable.builder()
                    .number("S" + counter++)
                    .type(TableType.SMALL)
                    .build());
        }

        if (!newTables.isEmpty()) {
            tableRepository.saveAll(newTables);
        }
    }
}
