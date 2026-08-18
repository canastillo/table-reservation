package com.restaurant.reservation.controller;

import com.restaurant.reservation.domain.enums.TableType;
import com.restaurant.reservation.dto.MessageResponse;
import com.restaurant.reservation.dto.TableInitializationRequest;
import com.restaurant.reservation.exception.ApiError;
import com.restaurant.reservation.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Table management", description = "Operations on tables.")
public class TableController {
    private final TableService tableService;

    @Operation(summary = "Initialize tables", description = "Deletes existing tables and inserts new ones based on the indicated amounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tables configured successfully",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data entry",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/initialize")
    public ResponseEntity<MessageResponse> initializeTables(@Valid @RequestBody TableInitializationRequest request) {
        log.info("Received POST request for tables.");
        tableService.initializeTables(request);
        return ResponseEntity.ok(new MessageResponse("Tables configured successfully."));
    }

    @Operation(summary = "Fetch existing tables", description = "Returns the current configuration of tables by type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tables fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "object",
                                    description = "Map with TableType enum as key and amount as value",
                                    implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping()
    public ResponseEntity<Map<TableType, Integer>> getRestaurantTables() {
        log.info("Received GET request for tables.");
        Map<TableType, Integer> allTables = tableService.getAllTables();
        return ResponseEntity.ok(allTables);
    }
}
