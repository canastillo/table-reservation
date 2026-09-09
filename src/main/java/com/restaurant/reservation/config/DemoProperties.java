package com.restaurant.reservation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("demo")
@ConfigurationProperties(prefix = "app.bootstrap")
public record DemoProperties(Admin admin, User user) {
    public record Admin(String email, String password) {}
    public record User(String email, String password) {}
}
