package com.restaurant.reservation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
@ConfigurationProperties(prefix = "app.bootstrap.admin")
public record ProdProperties(String email, String password, String fullName) {
}
