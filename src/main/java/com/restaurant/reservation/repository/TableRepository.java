package com.restaurant.reservation.repository;

import com.restaurant.reservation.domain.models.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
    Optional<RestaurantTable> findByNumber(String number);
    List<RestaurantTable> findByActiveTrue();
}
