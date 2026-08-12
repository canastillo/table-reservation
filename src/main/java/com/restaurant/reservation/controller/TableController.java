package com.restaurant.reservation.controller;

import com.restaurant.reservation.domain.enums.TableType;
import com.restaurant.reservation.dto.MessageResponse;
import com.restaurant.reservation.dto.TableInitializationRequest;
import com.restaurant.reservation.service.TableService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/tables")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin operations", description = "Configuración inicial del salón")
public class AdminController {
    private final TableService tableService;

    @PostMapping("/initialize")
    public ResponseEntity<MessageResponse> initializeTables(@Valid @RequestBody TableInitializationRequest request) {
        log.info("Received POST request for tables.");
        tableService.initializeTables(request);
        return ResponseEntity.ok(new MessageResponse("Tables configured successfully."));
    }

    @GetMapping()
    public ResponseEntity<Map<TableType, Integer>> getRestaurantTables() {
        log.info("Received GET request for tables.");
        Map<TableType, Integer> allTables = tableService.getAllTables();
        return ResponseEntity.ok(allTables);
    }
}
