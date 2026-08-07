package com.restaurant.reservation.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TableInitializationRequest {
    // these are the keys in the json
    @Min(0) private int fixed;
    @Min(0) private int large;
    @Min(0) private int small;
}
