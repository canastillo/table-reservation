package com.restaurant.reservation.domain.models;

import com.restaurant.reservation.domain.enums.TableType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_table", uniqueConstraints = {
        @UniqueConstraint(columnNames = "number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TableType type;

    @Builder.Default
    private boolean active = true;

    // TODO: Base capacity could be added based on TableType
}
