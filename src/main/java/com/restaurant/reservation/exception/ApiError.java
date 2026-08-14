package com.restaurant.reservation.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    @Schema(description = "HTTP status", example = "200")
    private int status;

    @Schema(description = "Message describing the error", example = "fixed: must be greater than or equal to 0")
    private String message;

    @Schema(description = "Timestamp when error happened in ISO-8601 UTC format", example = "2026-08-11T19:07:14Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime timestamp;

    @Schema(description = "Unique identifier for tracking errors", example = "0dde2a90-e1a0-4041-9f3f-5fa9e4fd1218")
    private String correlationId;
}
