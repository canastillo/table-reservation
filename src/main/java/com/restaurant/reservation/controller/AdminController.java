package com.restaurant.reservation.controller;

import com.restaurant.reservation.dto.MessageResponse;
import com.restaurant.reservation.dto.TableInitializationRequest;
import com.restaurant.reservation.service.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tables")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final TableService tableService;

    @PostMapping("/initialize")
    public ResponseEntity<MessageResponse> initializeTables(@Valid @RequestBody TableInitializationRequest request) {
        log.info("Received POST request for tables.");
        tableService.initializeTables(request);
        return ResponseEntity.ok(new MessageResponse("Mesas configuradas exitosamente"));
    }
}
