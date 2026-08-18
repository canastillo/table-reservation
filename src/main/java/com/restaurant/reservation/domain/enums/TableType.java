package com.restaurant.reservation.domain.enums;

public enum TableType {
    FIXED(7),
    LARGE(6),
    SMALL(4);

    public final int capacity;

    TableType(int capacity) {
        this.capacity = capacity;
    }
}
