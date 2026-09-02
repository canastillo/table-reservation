package com.restaurant.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type;
    private Long id;
    private String fullName;
    private String email;
    private List<String> roles;
}
