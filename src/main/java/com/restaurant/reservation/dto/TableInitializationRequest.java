package com.restaurant.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TableInitializationRequest {
    // these are the keys in the json
    @Schema(description = "Amount of fixed tables", example = "5", minimum = "0")
    @Min(0)
    private int fixed;

    @Schema(description = "Amount of large movable tables", example = "5", minimum = "0")
    @Min(0)
    private int large;

    @Schema(description = "Amount of small movable tables", example = "5", minimum = "0")
    @Min(0)
    private int small;
}
