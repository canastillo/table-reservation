package com.restaurant.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogInRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
