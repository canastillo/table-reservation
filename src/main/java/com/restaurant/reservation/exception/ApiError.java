package com.restaurant.reservation.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    @Schema(description = "HTTP status", example = "200")
    private int status;

    @Schema(description = "Message describing the error", example = "fixed: must be greater than or equal to 0")
    private String message;

    @Schema(description = "Timestamp when error happened in format ISO-8601 y UTC", example = "2026-08-11T19:07:14.15795012")
    private LocalDateTime timestamp;

    @Schema(description = "Unique identifier for tracking errors", example = "5", minimum = "0")
    private String correlationId;
}
