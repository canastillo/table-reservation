package com.restaurant.reservation.repository;

import com.restaurant.reservation.domain.enums.RoleType;
import com.restaurant.reservation.domain.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
