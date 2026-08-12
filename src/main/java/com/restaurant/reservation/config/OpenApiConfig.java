package com.restaurant.reservation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI restaurantOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Restaurant Reservation System API")
                        .description("API for table reservation management for restaurants.\n\n")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ana Castillo")
                                .url("https://github.com/canastillo/table-reservation")
                                .email("canastillo_@outlook.com")));
    }
}
