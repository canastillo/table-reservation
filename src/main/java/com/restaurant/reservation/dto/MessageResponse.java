package com.restaurant.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponse {
    @Schema(description = "Message containing the state of the request", example = "Tables configured successfully")
    private String message;
}
